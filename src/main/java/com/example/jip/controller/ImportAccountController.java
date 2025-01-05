package com.example.jip.controller;

import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountImportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("account/import")
public class ImportAccountController {

    @Autowired
    AccountImportServices accountImportServices;

    @PostMapping("/excel")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of("Invalid file format. Please upload an Excel file."));
        }
        try {
            List<String> errors = accountImportServices.importAccounts(file);

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            return ResponseEntity.ok(List.of("Data imported successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("Error occurred: " + e.getMessage()));
        }
    }
}

