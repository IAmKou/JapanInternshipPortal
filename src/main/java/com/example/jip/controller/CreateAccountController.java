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
        int acccount_id = accountServices.createAccount(username, password, role);
        RedirectView redirectView = new RedirectView();
        if (role == 2) {
            redirectView.setUrl("/create-account-student.html?account_id=" + acccount_id);

        } else if (role == 3) {
            redirectView.setUrl("/create-account-tm1.html?account_id=" + acccount_id);

        } else if (role == 4) {
            redirectView.setUrl("/create-account-tm1.html?account_id=" + acccount_id);
        } else {
            redirectView.setUrl("/login.html");
        }
        return redirectView;
    }
}
