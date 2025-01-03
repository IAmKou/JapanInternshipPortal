package com.example.jip.services;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final Translate translate;

    // Nhận API Key từ application.properties
    public TranslationService(@Value("${google.cloud.api.key}") String apiKey) {
        this.translate = TranslateOptions.newBuilder()
                .setApiKey(apiKey) // Sử dụng API Key
                .build()
                .getService();
    }

    // Hàm dịch văn bản
    public String translateText(String text, String targetLanguage) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage),
                Translate.TranslateOption.format("text")
        );
        return translation.getTranslatedText();
    }
}