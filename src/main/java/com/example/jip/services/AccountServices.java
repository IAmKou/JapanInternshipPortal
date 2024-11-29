// AccountServices.java
package com.example.jip.services;

import com.example.jip.dto.AccountDTO;
import com.example.jip.entity.*;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Role not found"));
        account.setRole(roleEntity);

        Account savedAccount = accountRepository.save(account);
        return savedAccount.getId();
    }

    public List<AccountDTO> getAllAccountDTOs() {
        List<Account> accounts = (List<Account>) accountRepository.findAll();
        return accounts.stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());
    }

    public AccountDTO updateAccount(int id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found with id " + id));
        if (account.getStudent() != null) {
            Student student = account.getStudent();
            student.setFullname(accountDTO.getFullName());
            student.setEmail(accountDTO.getEmail());
            student.setPhoneNumber(accountDTO.getPhoneNumber());
            student.setDob(accountDTO.getDob());
            student.setJapanname(accountDTO.getJname());
            student.setGender(student.getGender());
        } else if (account.getTeacher() != null) {
            Teacher teacher = account.getTeacher();
            teacher.setFullname(accountDTO.getFullName());
            teacher.setEmail(accountDTO.getEmail());
            teacher.setPhoneNumber(accountDTO.getPhoneNumber());
            teacher.setJname(accountDTO.getJname());
            teacher.setGender(teacher.getGender());
        } else if (account.getManager() != null) {
            Manager manager = account.getManager();
            manager.setFullname(accountDTO.getFullName());
            manager.setEmail(accountDTO.getEmail());
            manager.setPhoneNumber(accountDTO.getPhoneNumber());
            manager.setJname(accountDTO.getJname());
            manager.setGender(manager.getGender());
        }
        accountRepository.save(account);

        return new AccountDTO(account);
    }
}
