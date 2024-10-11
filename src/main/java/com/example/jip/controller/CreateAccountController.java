package com.example.jip.controller;

import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    @Autowired
    private AccountServices accountServices;

    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account, @RequestParam String role) {
        return accountServices.saveAccount(account,role);
    }
}
