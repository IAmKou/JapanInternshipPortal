package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TeacherServices {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailServices emailServices;


    public Teacher createTeacher(String fullname, String jname, String email, String phoneNumber, String gender, MultipartFile img, int account_id, String password) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        String folderName = sanitizeFolderName("Account/Teacher/" + accountOpt.get().getUsername());

        String imgUrl = s3Service.uploadFile(img, folderName, img.getOriginalFilename());



        Teacher teacher = new Teacher();
        teacher.setFullname(fullname);
        teacher.setJname(jname);
        teacher.setEmail(email);
        teacher.setPhoneNumber(phoneNumber);
        teacher.setGender(Teacher.gender.valueOf(gender));
        teacher.setImg(imgUrl);
        teacher.setAccount(accountOpt.get());
        Teacher savedTeacher = teacherRepository.save(teacher);



        CompletableFuture.runAsync(() -> {
            String emailStatus = emailServices.sendEmail(email,password);
            if (emailStatus == null) {
                System.out.println("Failed to send email to: " + email);
            } else {
                System.out.println("Email sent successfully to: " + email);
            }
        });

        return savedTeacher;

    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }
    private boolean isDuplicate(String email, String phoneNumber) {
        return teacherRepository.findByEmail(email).isPresent() || teacherRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
