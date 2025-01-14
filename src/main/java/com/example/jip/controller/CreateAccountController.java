package com.example.jip.controller;

import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.services.AccountServices;
import com.example.jip.services.ManagerServices;
import com.example.jip.services.StudentServices;
import com.example.jip.services.TeacherServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    @Autowired
    private AccountServices accountServices;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentServices studentServices;

    @Autowired
    private TeacherServices teacherServices;

    @Autowired
    private ManagerServices managerServices;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            @RequestParam int role,
            @RequestParam String fullname,
            @RequestParam String japanname,
            @RequestParam String email,
            @RequestParam String phoneNumber,
            @RequestParam String gender,
            @RequestParam(required = false) String dob, // for Students
            @RequestParam MultipartFile img
    ) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Full name is required");
        }

        // Validate Japan name
        if (japanname == null || japanname.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Japan name is required");
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        // Check if email already exists
        if (accountRepository.existsByUsername(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required");
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            return ResponseEntity.badRequest().body("Invalid phone number format");
        }

        // Validate gender
        if (gender == null || gender.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Gender is required");
        }

        if (accountRepository.existsByUsername(email)) {
            return ResponseEntity.badRequest().body("Email already exist");
        }
        try {
            Role roles = roleRepository.findById(role)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            String password = generateVerifyCode();

            int accountId = accountServices.createAccount(email, password, role);

            // Save role-specific information
            switch (roles.getName()) {
                case "STUDENT":
                    LocalDate localDate;
                    Date date;
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        localDate = LocalDate.parse(dob, formatter);
                        date = Date.valueOf(localDate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("Invalid date format. Please use 'yyyy-MM-dd'.");
                    }
                    studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, accountId, password);
                    break;
                case "TEACHER":
                    teacherServices.createTeacher(fullname, japanname, email, phoneNumber, gender, img, accountId, password);
                    break;
                case "MANAGER":
                    managerServices.createManager(fullname, japanname, email, phoneNumber, gender, img, accountId, password);
                    break;
            }

            return ResponseEntity.ok(accountId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account: " + e.getMessage());
        }
    }

    private String generateVerifyCode() {
        int code = (int) (Math.random() * 1000000);  // Generates a 6-digit random code
        return String.format("%06d", code);  // Ensure it's always 6 digits
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?[0-9]{10,15}$"; // Adjust the regex based on phone number format
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }
}
