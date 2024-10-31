package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.entity.Student;
import com.example.jip.entity.Student.Gender;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.repository.StudentRepository;
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

    private final List<String> errors = new ArrayList<>();

    @Transactional
    public void importAccounts(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                processRow(row);
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException("Import completed with errors: " + errors);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process file: " + e.getMessage());
        }
    }

    private void processRow(Row row) {
        try {
            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            int roleId = (int) row.getCell(2).getNumericCellValue();
            String fullName = row.getCell(3).getStringCellValue();
            String japanname = row.getCell(4).getStringCellValue();
            LocalDate dob = row.getCell(5).getLocalDateTimeCellValue().toLocalDate();
            String passportUrl = row.getCell(6).getStringCellValue();
            Gender gender = Gender.valueOf(row.getCell(7).getStringCellValue());
            String phoneNumber = row.getCell(8).getStringCellValue();
            String img = row.getCell(9).getStringCellValue();
            String email = row.getCell(10).getStringCellValue();

            if (isDuplicate(username, email, phoneNumber)) return;

            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                errors.add("Role ID " + roleId + " does not exist for user " + username);
                return;
            }

            // Save Account
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(roleOpt.get());
            accountRepository.save(account);

            // Save Student
            Student student = new Student();
            student.setFullname(fullName);
            student.setJapanname(japanname);
            student.setDob(java.sql.Date.valueOf(dob));
            student.setPassport(passportUrl);
            student.setGender(gender);
            student.setPhoneNumber(phoneNumber);
            student.setImg(img);
            student.setEmail(email);
            student.setAccountId(account.getId());
            studentRepository.save(student);

        } catch (Exception e) {
            errors.add("Failed to process row: " + row.getRowNum() + " due to: " + e.getMessage());
        }
    }

    private boolean isDuplicate(String username, String email, String phoneNumber) {
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