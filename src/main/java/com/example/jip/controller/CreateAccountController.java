package com.example.jip.controller;

import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    @Autowired
    private AccountServices accountServices;

    @PostMapping("/create")
    public ResponseEntity<Integer> createAccount(@RequestParam String username
    , @RequestParam String password
    , @RequestParam int role) {
        int accountId = accountServices.createAccount(username,password,role);
        return ResponseEntity.ok(accountId);

    }
}
