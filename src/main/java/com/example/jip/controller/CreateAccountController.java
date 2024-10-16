package com.example.jip.controller;

import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/accounts")
public class CreateAccountController {

    @Autowired
    private AccountServices accountServices;

    @PostMapping("/create")
    public RedirectView createAccount(@RequestParam String username
    , @RequestParam String password
    , @RequestParam int role) {
        int id = accountServices.createAccount(username,password,role);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/create-account-student.html?id="+id);
        return redirectView;
    }
}
