package com.example.jip.controller;

import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LoginController {
@Autowired
     private AccountRepository accountRepository;

  @PostMapping("/login")
    public @ResponseBody String login(@RequestParam String username, @RequestParam String password) {
      List<Account> accounts = accountRepository.findByUsernameAndPassword(username, password);
      if (accounts.isEmpty()) {
          return "Login failed: Invalid username or password";
      } else {
          
          return "Login successful for user: " + accounts.get(0).getUsername();
      }


  }
}
