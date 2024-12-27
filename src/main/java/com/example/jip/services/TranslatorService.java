package com.example.jip.services;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class TranslatorService {

    public String translate(String text, String sourceLang, String targetLang) {
        String googleScriptUrl = "https://script.google.com/macros/s/AKfycbxTEGQZ2TVpgWJyA4EQbb9umUFJB7ydvtn078FWBV8E0nIRVZZVi36ON--Z8ZwhbrXz/exec"; // Thay bằng URL của bạn

        // Xây dựng URL với tham số
        String url = UriComponentsBuilder.fromHttpUrl(googleScriptUrl)
                .queryParam("text", text)
                .queryParam("source", sourceLang)
                .queryParam("target", targetLang)
                .toUriString();

        // Gửi yêu cầu và nhận phản hồi
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response; // Phản hồi dạng JSON chứa văn bản đã dịch
    }
}
