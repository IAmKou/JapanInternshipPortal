package com.example.jip.controller;

import com.example.jip.services.LibreTranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslatorController {

    @Autowired
    private LibreTranslateService libreTranslateService;

    @GetMapping("/translate")
    public String translate(@RequestParam String text, @RequestParam String targetLanguage) {
        if (text == null || text.isEmpty()) {
            return "Error: Text cannot be empty!";
        }
        if (targetLanguage == null || targetLanguage.isEmpty()) {
            return "Error: Target language cannot be empty!";
        }

        try {
            // Kiểm tra kết quả từ dịch vụ
            String translation = libreTranslateService.translateText(text, targetLanguage);
            if (translation == null || translation.isEmpty()) {
                return "Error: No translation available!";
            }
            return translation;
        } catch (Exception e) {
            return "Error during translation: " + e.getMessage();
        }
    }
}