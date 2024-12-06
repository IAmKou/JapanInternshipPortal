package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Manager;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ManagerServices {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    public Manager createManager(String fullname, String jname, String email, String phoneNumber, String gender, MultipartFile img, int account_id) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        String imgUrl = cloudinaryService.uploadFileToFolder(img, "Account/").getUrl();

        Manager manager = new Manager();
        manager.setFullname(fullname);
        manager.setJname(jname);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setGender(Manager.Gender.valueOf(gender));
        manager.setImg(imgUrl);
        manager.setAccount(accountOpt.get());
        return managerRepository.save(manager);

    }

    private boolean isDuplicate(String email, String phoneNumber) {
        return managerRepository.findByEmail(email).isPresent() || managerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
