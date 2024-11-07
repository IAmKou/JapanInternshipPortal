package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Manager;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerServices {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AccountRepository accountRepository;


    public Manager createManager(String fullname, String jname, String email, String phoneNumber, String gender, String img, int account_id) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        Manager manager = new Manager();
        manager.setFullname(fullname);
        manager.setJname(jname);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setGender(Manager.Gender.valueOf(gender));
        manager.setImg(img);
        manager.setAccount(accountOpt.get());
        return managerRepository.save(manager);

    }

}
