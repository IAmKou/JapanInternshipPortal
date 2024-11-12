package com.example.jip.services;

import com.example.jip.dto.AccountDTO;
import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class UserServices {
    @Autowired
    private AccountRepository accountRepository;

    public AccountDTO getProfileById(int id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found with id " + id));
         return new AccountDTO(account);
    }

}
