package com.example.jip.controller;

import com.example.jip.services.LibreTranslateService;
import com.example.jip.services.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @PostMapping("/whole-page")
    public ResponseEntity<Map<String, Object>> translateWholePage(@RequestBody Map<String, Object> requestBody) {
        String text = (String) requestBody.get("text");
        String targetLanguage = (String) requestBody.get("targetLanguage");

        // Kiểm tra nếu tham số thiếu
        if (text == null || text.trim().isEmpty() || targetLanguage == null || targetLanguage.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Thiếu tham số hoặc tham số không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Lấy các văn bản động như userFullName, userRole, v.v.
        Map<String, String> extraText = (Map<String, String>) requestBody.get("extraText");

        try {
            // Dịch nội dung chính của trang
            String translatedText = translationService.translateText(text, targetLanguage);

            // Dịch các phần tử văn bản động
            Map<String, String> extraTranslations = new HashMap<>();
            if (extraText != null) {
                for (Map.Entry<String, String> entry : extraText.entrySet()) {
                    String translatedValue = translationService.translateText(entry.getValue(), targetLanguage);
                    extraTranslations.put(entry.getKey(), translatedValue);
                }
            }

            // Trả về kết quả dịch bao gồm nội dung và các phần tử động
            Map<String, Object> response = new HashMap<>();
            response.put("translatedText", translatedText);
            response.put("extraTranslations", extraTranslations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về lỗi nếu có vấn đề trong quá trình dịch
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi dịch trang.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}