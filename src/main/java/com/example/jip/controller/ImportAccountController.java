package com.example.jip.controller;

import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountImportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("account/import")
public class ImportAccountController {
    @Autowired
    private AccountImportServices accountImportServices;

    @PostMapping("/excel")
    public ResponseEntity<String> importFile(@RequestParam ("file")
            MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file format. Please upload an Excel file.");
        }

        try {
            accountImportServices.importAccounts(file);
            return ResponseEntity.ok("Data imported successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }

    }
}
