package com.example.jip.controller;

import com.example.jip.configuration.CustomUserDetail;
import com.example.jip.dto.AccountDTO;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserApiController {
    @Autowired
    private UserServices userServices;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/user-detail")
    public AccountDTO getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            int accountId = userDetails.getAccountId();
            return userServices.getProfileById(accountId);
        }
        return null;
    }

    @GetMapping("/{userId}/userDetail")
    public AccountDTO getUser(@PathVariable int userId) {
        return userServices.getProfileById(userId);
    }
}
