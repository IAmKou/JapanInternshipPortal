package com.example.jip.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LibreTranslateService {

    private final WebClient webClient;

    // Constructor dependency injection
    public LibreTranslateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://libretranslate.com/translate").build(); // API base URL
    }

    // Hàm dịch văn bản
    public String translateText(String text, String targetLanguage) {
        return webClient.post()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("q=" + text + "&target=" + targetLanguage)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), response -> {
                    System.out.println("Server error: " + response.statusCode());
                    return Mono.empty();
                })
                .onStatus(status -> status.is4xxClientError(), response -> {
                    System.out.println("Client error: " + response.statusCode());
                    return Mono.empty();
                })
                .bodyToMono(String.class)
                .map(response -> {
                    System.out.println("Received response: " + response);
                    return response;
                })
                .doOnError(e -> System.out.println("Error during translation request: " + e.getMessage()))
                .block(); // Dùng block() để chuyển đổi Mono thành giá trị đồng bộ
    }
}