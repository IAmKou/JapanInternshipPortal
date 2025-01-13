package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Manager;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ManagerServices {
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailServices emailServices;


    public Manager createManager(String fullname, String jname, String email, String phoneNumber, String gender, MultipartFile img, int account_id, String password) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        String folderName = sanitizeFolderName("Account/Manager/" + accountOpt.get().getUsername());

        CompletableFuture<String> imgUrlFuture = CompletableFuture.supplyAsync(() ->
                s3Service.uploadFile(img, folderName, img.getOriginalFilename())
        );

        Manager manager = new Manager();
        manager.setFullname(fullname);
        manager.setJname(jname);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setGender(Manager.Gender.valueOf(gender));
        manager.setAccount(accountOpt.get());
        Manager savedManager = managerRepository.save(manager);

        try {
            manager.setImg(imgUrlFuture.get());
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }

        CompletableFuture.runAsync(() -> {
            String emailStatus = emailServices.sendEmail(password, accountOpt.get().getUsername());
            if (emailStatus == null) {
                System.out.println("Failed to send email to: " + email);
            } else {
                System.out.println("Email sent successfully to: " + email);
            }
        });
        return savedManager;

    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }

    private boolean isDuplicate(String email, String phoneNumber) {
        return managerRepository.findByEmail(email).isPresent() || managerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
