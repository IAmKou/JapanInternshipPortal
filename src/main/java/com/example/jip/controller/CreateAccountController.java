package com.example.jip.controller;

import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    @Autowired
    private AccountServices accountServices;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestParam String username,
                                           @RequestParam int role) {

        Role roleEntity = roleRepository.findById(role)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Check if there are already 3 admins
        if (roleEntity.getName().equals("ADMIN") && accountRepository.countByRole(roleEntity) >= 3) {
            return ResponseEntity.badRequest().body("Cannot create more than 3 admin accounts");
        }
        if (accountRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        try {
            String password = generateVerifyCode();
            int accountId = accountServices.createAccount(username, password, role);

            return ResponseEntity.ok(accountId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account");
        }
    }

    private String generateVerifyCode() {
        int code = (int) (Math.random() * 1000000);  // Generates a 6-digit random code
        return String.format("%06d", code);  // Ensure it's always 6 digits
    }
}
