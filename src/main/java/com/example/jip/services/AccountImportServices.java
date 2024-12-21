package com.example.jip.services;

import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.entity.Student;
import com.example.jip.entity.Student.Gender;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.repository.StudentRepository;
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

    public List<String> importAccounts(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    processRow(row, errors, workbook);
                } catch (Exception e) {
                    errors.add("Error in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("File processing error: " + e.getMessage());
            e.printStackTrace();
        }
        return errors;
    }

    private void processRow(Row row, List<String> errors, XSSFWorkbook workbook) {
        if (isRowEmpty(row)) {
            return;
        }

        try {
            DataFormatter dataFormatter = new DataFormatter(); // Ensure consistent parsing of cell data

            // Username
            String username = dataFormatter.formatCellValue(row.getCell(0)).trim();
            if (username.isEmpty()) {
                errors.add("Missing username at row " + (row.getRowNum() + 1));
                return;
            }

            // Password
            String password = dataFormatter.formatCellValue(row.getCell(1)).trim();
            if (password.isEmpty()) {
                errors.add("Missing password at row " + (row.getRowNum() + 1));
                return;
            }

            // Role ID
            int roleId;
            try {
                roleId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(2)).trim());
            } catch (NumberFormatException e) {
                errors.add("Invalid role ID at row " + (row.getRowNum() + 1));
                return;
            }

            // Full Name
            String fullName = dataFormatter.formatCellValue(row.getCell(3)).trim();
            if (fullName.isEmpty()) {
                errors.add("Missing full name at row " + (row.getRowNum() + 1));
                return;
            }

            // Japan Name
            String japanname = dataFormatter.formatCellValue(row.getCell(4)).trim();
            if (japanname.isEmpty()) {
                errors.add("Missing Japan name at row " + (row.getRowNum() + 1));
                return;
            }

            // Date of Birth
            LocalDate dob;
            Cell dobCell = row.getCell(5);
            if (dobCell == null) {
                errors.add("Missing date of birth at row " + (row.getRowNum() + 1));
                return;
            }
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
            } else {
                errors.add("Invalid date of birth format at row " + (row.getRowNum() + 1));
                return;
            }


            // Gender
            Gender gender;
            try {
                gender = Gender.valueOf(dataFormatter.formatCellValue(row.getCell(7)).trim());
            } catch (IllegalArgumentException e) {
                errors.add("Invalid gender at row " + (row.getRowNum() + 1));
                return;
            }

            // Phone Number
            String phoneNumber = dataFormatter.formatCellValue(row.getCell(8)).trim();
            if (phoneNumber.isEmpty()) {
                errors.add("Missing phone number at row " + (row.getRowNum() + 1));
                return;
            }


            // Email
            String email = dataFormatter.formatCellValue(row.getCell(10)).trim();
            if (email.isEmpty()) {
                errors.add("Missing email at row " + (row.getRowNum() + 1));
                return;
            }
            String passportUrl = row.getCell(6).getStringCellValue();
            String passport = uploadImageToCloudinary(passportUrl, workbook);
            String imgPath = row.getCell(9).getStringCellValue();
            String imgUrl = uploadImageToCloudinary(imgPath, workbook);


            // Validate fields
            if (validateColumn(username, password, email, phoneNumber, String.valueOf(gender), fullName, japanname, errors, row.getRowNum())) {
                return;
            }
            if (isDuplicate(username, email, phoneNumber, errors, row.getRowNum())) {
                return;
            }




            // Find role
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                errors.add("Role ID " + roleId + " does not exist for user " + username);
                return;
            }

            // Save account
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(roleOpt.get());
            accountRepository.save(account);

            // Save student
            Student student = new Student();
            student.setFullname(fullName);
            student.setJapanname(japanname);
            student.setDob(Date.valueOf(dob));
            student.setPassport(passport);
            student.setGender(gender);
            student.setPhoneNumber(phoneNumber);
            student.setImg(imgUrl);
            student.setEmail(email);
            student.setAccount(account);
            student.setMark(false);
            studentRepository.save(student);

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
                CloudinaryResponse response = cloudinaryService.uploadFileToFolder(imageFile, "Account/"); // Upload to Cloudinary and return URL
                return response.getUrl();
            }

        } catch (Exception e) {
            throw new RuntimeException("Image extraction or upload failed: " + e.getMessage());
        }
        return null;
    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
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
