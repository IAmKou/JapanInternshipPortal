package com.example.jip.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class TranslationService {

    @Autowired
    private RestTemplate restTemplate;

    // URL API MyMemory
    private static final String MY_MEMORY_API_URL = "https://api.mymemory.translated.net/get";

    // Dịch văn bản từ ngôn ngữ này sang ngôn ngữ khác
    public String translate(String text, String langFrom, String langTo) {
        // Tạo URL cho API request
        String url = String.format("%s?q=%s&langpair=%s|%s", MY_MEMORY_API_URL, text, langFrom, langTo);

        // Thực hiện yêu cầu GET
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Trả về kết quả dịch từ API
        if (response.getStatusCode().is2xxSuccessful()) {
            return parseTranslation(response.getBody());
        } else {
            return "Dịch không thành công";
        }
    }

    // Phân tích JSON và lấy ra bản dịch
    private String parseTranslation(String response) {
        try {
            // Sử dụng Jackson hoặc Gson để parse JSON
            // Tạo ObjectMapper để phân tích JSON trả về từ MyMemory
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);
            JsonNode translatedText = node.path("responseData").path("translatedText");
            return translatedText.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error when parsing the response";
        }
    }
}