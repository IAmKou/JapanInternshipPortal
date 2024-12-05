package com.example.jip.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationCodeServices {

    private final Map<String, String> codeStorage = new HashMap<>(); // Maps username to code
    private String currentUsername;

    public void saveVerificationCode(String username, String code) {
        codeStorage.put(username, code);
    }

    public boolean validateCode(String username, String code) {
        return code.equals(codeStorage.get(username));
    }

    public void clearVerificationCode(String username) {
        codeStorage.remove(username);
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}
