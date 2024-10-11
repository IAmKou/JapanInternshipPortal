package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServices {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    public Account saveAccount(Account account, String roleName) {

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        Role role = roleRepository.findByName(roleName).orElseThrow(() ->
                new RuntimeException("Role not found"));
        account.setRole(role);


        return accountRepository.save(account);
    }
}
