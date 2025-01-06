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

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailServices emailServices;


    public Manager createManager(String fullname, String jname, String email, String phoneNumber, String gender, MultipartFile img, int account_id) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }


        String imgUrl = s3Service.uploadFile(img, "Account/Manager/" + fullname, img.getOriginalFilename());

        Manager manager = new Manager();
        manager.setFullname(fullname);
        manager.setJname(jname);
        manager.setEmail(email);
        manager.setPhoneNumber(phoneNumber);
        manager.setGender(Manager.Gender.valueOf(gender));
        manager.setImg(imgUrl);
        manager.setAccount(accountOpt.get());
        Manager savedManager = managerRepository.save(manager);

//        String account = accountOpt.get().getUsername();
//        String password = accountOpt.get().getPassword();
//
//        String emailStatus = emailServices.sendEmail(email, password, account);
//        if (emailStatus == null) {
//            System.out.println("Failed to send email to: " + email);
//        } else {
//            System.out.println("Email sent successfully to: " + email);
//        }

        return savedManager;

    }

    private boolean isDuplicate(String email, String phoneNumber) {
        return managerRepository.findByEmail(email).isPresent() || managerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
