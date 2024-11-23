package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.entity.Student;
import com.example.jip.entity.Student.Gender;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.repository.StudentRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
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
            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();

            int roleId = row.getCell(2).getCellType() == CellType.NUMERIC
                    ? (int) row.getCell(2).getNumericCellValue()
                    : Integer.parseInt(row.getCell(2).getStringCellValue());

            String fullName = row.getCell(3).getStringCellValue();
            String japanname = row.getCell(4).getStringCellValue();

            LocalDate dob = row.getCell(5).getCellType() == CellType.NUMERIC
                    ? row.getCell(5).getLocalDateTimeCellValue().toLocalDate()
                    : LocalDate.parse(row.getCell(5).getStringCellValue());

            String passportUrl = row.getCell(6).getStringCellValue();
            Gender gender = Gender.valueOf(row.getCell(7).getStringCellValue());

            String phoneNumber = row.getCell(8).getCellType() == CellType.NUMERIC
                    ? String.valueOf((long) row.getCell(8).getNumericCellValue())
                    : row.getCell(8).getStringCellValue();

            // Extract image path or URL from Excel
            String imgPath = row.getCell(9).getStringCellValue(); // Assuming the image is in column 9
            String imgUrl = uploadImageToCloudinary(imgPath, workbook); // Pass workbook to extract embedded images

            String email = row.getCell(10).getStringCellValue();

            if (isDuplicate(username, email, phoneNumber, errors)) return;

            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                errors.add("Role ID " + roleId + " does not exist for user " + username);
                return;
            }

            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(roleOpt.get());
            accountRepository.save(account);

            Student student = new Student();
            student.setFullname(fullName);
            student.setJapanname(japanname);
            student.setDob(java.sql.Date.valueOf(dob));
            student.setPassport(passportUrl);
            student.setGender(gender);
            student.setPhoneNumber(phoneNumber);
            student.setImg(imgUrl);  // Save Cloudinary URL for the image
            student.setEmail(email);
            student.setAccount(account);
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
            if (imageBytes != null) {
                MultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
                return cloudinaryService.uploadImage(imageFile); // Upload to Cloudinary and return URL
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

    private boolean isDuplicate(String username, String email, String phoneNumber, List<String> errors) {
        boolean hasError = false;
        if (accountRepository.findByUsername(username).isPresent()) {
            errors.add("Duplicate username found: " + username);
            hasError = true;
        }
        if (studentRepository.findByEmail(email).isPresent()) {
            errors.add("Duplicate email found: " + email);
            hasError = true;
        }
        if (studentRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            errors.add("Duplicate phone number found: " + phoneNumber);
            hasError = true;
        }
        return hasError;
    }
}
