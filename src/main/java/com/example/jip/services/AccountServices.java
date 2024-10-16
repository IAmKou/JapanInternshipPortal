package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServices {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public int createAccount(String username, String password, int role) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));

        Role roleEntity = roleRepository.findById(role)
                .orElseThrow(()-> new RuntimeException("Role not found"));
        account.setRole(roleEntity);

        Account savedAccount = accountRepository.save(account);
        return savedAccount.getId();
    }



}
