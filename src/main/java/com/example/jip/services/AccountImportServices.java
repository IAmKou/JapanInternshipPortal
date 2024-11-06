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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class AccountImportServices {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RoleRepository roleRepository;  // Injecting RoleRepository
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<String> importAccounts(MultipartFile file) {
        List<String> errors = new ArrayList<>();  // Initialize a new list to collect errors

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    processRow(row, errors);
                } catch (Exception e) {
                    errors.add("Error in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("File processing error: " + e.getMessage());
            e.printStackTrace();
        }

        return errors; // Return the list of errors, if any
    }

    private void processRow(Row row, List<String> errors) {
        if (isRowEmpty(row)) {
            return;
        }

        try {
            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();

            // Handle Role ID as either String or Numeric
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

            String img = row.getCell(9).getStringCellValue();
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
            student.setImg(img);
            student.setEmail(email);
            student.setAccount(account);
            studentRepository.save(student);

        } catch (Exception e) {
            errors.add("Failed to process row: " + (row.getRowNum() + 1) + " due to: " + e.getMessage());
        }
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