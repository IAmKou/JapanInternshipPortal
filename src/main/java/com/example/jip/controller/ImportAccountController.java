package com.example.jip.controller;

import com.example.jip.services.AccountImportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("account/import")
public class ImportAccountController {

    @Autowired
    AccountImportServices accountImportServices;

    @Autowired
    MessageSource messageSource; // Sử dụng MessageSource

    @PostMapping("/excel")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Accept-Language", defaultValue = "en") String lang) {
        Locale locale = new Locale(lang);  // Lấy Locale từ header Accept-Language

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            String errorMessage = messageSource.getMessage("file.invalidFormat", null, locale);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(errorMessage));
        }

        try {
            List<String> errors = accountImportServices.importAccounts(file);

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            // Trả về thông điệp quốc tế hóa
            String successMessage = messageSource.getMessage("file.importSuccess", null, locale);
            return ResponseEntity.ok(List.of(successMessage));
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("error.general", new Object[] {e.getMessage()}, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(errorMessage));
        }
    }
}
