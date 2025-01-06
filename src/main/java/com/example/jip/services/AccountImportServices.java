package com.example.jip.services;

import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.*;
import com.example.jip.entity.Student.Gender;
import com.example.jip.repository.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
@Service
public class AccountImportServices {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private MarkReportRepository markReportRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private MarkRpExamRepository markRpExamRepository;
    @Autowired
    private EmailServices emailServices;

    public List<String> importAccounts(MultipartFile file) {
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        if (file.isEmpty()) {
            errors.add("The uploaded file is empty. Please upload a valid Excel file.");
            return errors;
        }

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errors.add("The uploaded Excel file is missing a sheet or is not properly formatted.");
                return errors;
            }

            // Validate column headers
            if (!validateHeaders(sheet.getRow(0), errors)) {
                return errors; // If headers are invalid, terminate processing
            }

            List<Row> rows = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (!isRowEmpty(row)) {
                    rows.add(row);
                }
            }

            if (rows.isEmpty()) {
                errors.add("The uploaded file contains no data to process.");
                return errors;
            }

            // Multithreading setup
            int threads = Runtime.getRuntime().availableProcessors(); // Number of available CPU cores
            ExecutorService executorService = Executors.newFixedThreadPool(threads);

            int chunkSize = rows.size() / threads;
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < threads; i++) {
                int start = i * chunkSize;
                int end = (i == threads - 1) ? rows.size() : (i + 1) * chunkSize;

                List<Row> chunk = rows.subList(start, end);

                Callable<Void> task = () -> {
                    for (Row row : chunk) {
                        try {
                            processRow(row, errors, workbook);
                        } catch (Exception e) {
                            errors.add("Error in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                        }
                    }
                    return null;
                };

                futures.add(executorService.submit(task));
            }

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get(); // Wait for the task to complete
                } catch (ExecutionException | InterruptedException e) {
                    errors.add("Error in multithreaded execution: " + e.getMessage());
                }
            }

            executorService.shutdown();

        } catch (Exception e) {
            errors.add("File processing error: " + e.getMessage());
            e.printStackTrace();
        }

        return errors;
    }

    private boolean validateHeaders(Row headerRow, List<String> errors) {
        if (headerRow == null) {
            errors.add("Missing header row. Ensure the first row contains column headers.");
            return false;
        }

        String[] requiredHeaders = {
                "Username", "Password", "Role ID", "Full Name", "Japan Name",
                "Date of Birth", "Image", "Gender", "Phone Number", "Passport", "Email"
        };

        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < requiredHeaders.length; i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = cell != null ? dataFormatter.formatCellValue(cell).trim() : "";
            if (!headerValue.equalsIgnoreCase(requiredHeaders[i])) {
                errors.add("Invalid or missing header at column " + (i + 1) + ": Expected '" + requiredHeaders[i] + "'");
                return false;
            }
        }
        return true;
    }

    private void processRow(Row row, List<String> errors, XSSFWorkbook workbook) {
        if (isRowEmpty(row)) {
            return;
        }

        try {
            DataFormatter dataFormatter = new DataFormatter(); // Ensure consistent parsing of cell data

            // Validate basic fields
            String username = dataFormatter.formatCellValue(row.getCell(0)).trim();
            String password = dataFormatter.formatCellValue(row.getCell(1)).trim();
            String fullName = dataFormatter.formatCellValue(row.getCell(3)).trim();
            String japanName = dataFormatter.formatCellValue(row.getCell(4)).trim();
            String phoneNumber = dataFormatter.formatCellValue(row.getCell(8)).trim();
            String email = dataFormatter.formatCellValue(row.getCell(10)).trim();

            // Validate date of birth
            LocalDate dob = null;
            Cell dobCell = row.getCell(5);
            if (dobCell != null) {
                if (dobCell.getCellType() == CellType.NUMERIC) {
                    dob = dobCell.getLocalDateTimeCellValue().toLocalDate();
                } else if (dobCell.getCellType() == CellType.STRING) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dob = LocalDate.parse(dobCell.getStringCellValue().trim(), formatter);
                    } catch (Exception e) {
                        errors.add("Invalid date format at row " + (row.getRowNum() + 1));
                        return;
                    }
                }
            }

            // Validate gender
            String genderStr = dataFormatter.formatCellValue(row.getCell(7)).trim();
            Gender gender;
            try {
                gender = Gender.valueOf(genderStr);
            } catch (IllegalArgumentException e) {
                errors.add("Invalid gender at row " + (row.getRowNum() + 1));
                return;
            }

            // Basic validations
            if (validateColumn(username, password, email, phoneNumber, genderStr, fullName, japanName, errors, row.getRowNum())) {
                return;
            }

            // Check for duplicates
            if (isDuplicate(username, email, phoneNumber, errors, row.getRowNum())) {
                return;
            }

            // Upload images to Cloudinary only after successful validation
            String passportUrl = row.getCell(9).getStringCellValue();
            String passport = uploadImageToCloudinary(passportUrl, workbook);

            String imgPath = row.getCell(6).getStringCellValue();
            String imgUrl = uploadImageToCloudinary(imgPath, workbook);

            // Find role
            int roleId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(2)).trim());
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                errors.add("Role ID " + roleId + " does not exist for user " + username);
                return;
            }

            // Save account and student after all validations and image uploads
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(roleOpt.get());
            accountRepository.save(account);

            Student student = new Student();
            student.setFullname(fullName);
            student.setJapanname(japanName);
            student.setDob(Date.valueOf(dob));
            student.setPassport(passport);
            student.setGender(gender);
            student.setPhoneNumber(phoneNumber);
            student.setImg(imgUrl);
            student.setEmail(email);
            student.setAccount(account);
            student.setMark(false);
            studentRepository.save(student);

            // Create MarkReport and link with Exams
            MarkReport markReport = new MarkReport();
            markReport.setStudent(student);
            markReportRepository.save(markReport);

            List<Exam> examList = examRepository.findAll();
            for (Exam exam : examList) {
                MarkReportExam markRpExam = new MarkReportExam(markReport, exam);
                markRpExamRepository.save(markRpExam);
            }
            emailServices.sendEmail(email,username,password);
        } catch (Exception e) {
            errors.add("Failed to process row: " + (row.getRowNum() + 1) + " due to: " + e.getMessage());
        }
    }



    private String uploadImageToCloudinary(String imgPath, XSSFWorkbook workbook) {
        try {
            // If the image path is not a URL but embedded in the Excel, extract it.
            if (imgPath != null && imgPath.startsWith("http")) {
                return imgPath; // If it's a URL, return it directly.
            }

            // Extract image from workbook if it's embedded
            byte[] imageBytes = getImageBytesFromExcel(workbook);
            //Em luong co the check lai ham vs sua lai file excel nhe, e vinh test add dc vao file r
            if (imageBytes != null) {
                MultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
                String response = s3Service.uploadFile(imageFile, "Account/", imageFile.getOriginalFilename()); // Upload to Cloudinary and return URL
                return response;
            }

        } catch (Exception e) {
            throw new RuntimeException("Image extraction or upload failed: " + e.getMessage());
        }
        return null;
    }


    private byte[] getImageBytesFromExcel(XSSFWorkbook workbook) {
        for (XSSFPictureData pictureData : workbook.getAllPictures()) {
            try {
                return pictureData.getData();  // Return first image found
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract image: " + e.getMessage());
            }
        }
        return null;  // If no image is found
    }


    private boolean isRowEmpty(Row row) {
        for (int i = 0; i <= 10; i++) {
            if (row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private boolean isDuplicate(String username, String email, String phoneNumber, List<String> errors, int row) {
        boolean hasError = false;
        if (accountRepository.findByUsername(username).isPresent()) {
            errors.add("Duplicate username: " + username + " found at row: " + row );
            hasError = true;
        }
        if (studentRepository.findByEmail(email).isPresent()) {
            errors.add("Duplicate email: " + email + " found at row: " + row);
            hasError = true;
        }
        if (studentRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            errors.add("Duplicate phone number: " + phoneNumber + " found at row: " + row);
            hasError = true;
        }
        return hasError;
    }

    private boolean validateColumn(String username, String password, String email, String phoneNumber,
                                   String gender, String fullname, String japanname, List<String> errors, int rowNum) {
        boolean isValid = false;

        // Validate username
        if (username == null || username.isEmpty()) {
            errors.add("Row " + rowNum + ": Username cannot be null or empty.");
            isValid = true;
        }

        // Validate password
        if (password == null || password.length() < 6) {
            errors.add("Row " + rowNum + ": Password must be at least 6 characters long.");
            isValid = true;
        }

        // Validate email
        if (email == null ) {
            errors.add("Row " + rowNum + ": Email can't be null");
            isValid = true;
        } else if (!email.matches("^[\\w.%+-]+@(gmail\\.com|fpt\\.edu\\.vn)$")) {
            errors.add("Row " + rowNum + ": Email must end with @gmail.com or @fpt.edu.vn");
            isValid = true;
        }

        // Validate phone number
        if (phoneNumber == null || !phoneNumber.matches("^0\\d{9,}$")) {
            errors.add("Row " + rowNum + ": Phone number must start with '0' and have at least 10 digits.");
            isValid = true;
        }

        // Validate gender
        if (gender == null || gender.isEmpty()) {
            errors.add("Row " + rowNum + ": Gender cannot be null or empty.");
            isValid = true;
        }

        // Validate full name
        if (fullname == null || fullname.isEmpty()) {
            errors.add("Row " + rowNum + ": Full name cannot be null or empty.");
            isValid = true;
        }

        // Validate Japanese name
        if (japanname == null || japanname.isEmpty()) {
            errors.add("Row " + rowNum + ": Japanese name cannot be null or empty.");
            isValid = true;
        }

        return isValid;
    }
}