package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class TeacherServices {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    public Teacher createTeacher(String fullname, String jname, String email, String phoneNumber, String gender, MultipartFile img, int account_id) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        String imgUrl = cloudinaryService.uploadFileToFolder(img, "Account/").getUrl();

        Teacher teacher = new Teacher();
        teacher.setFullname(fullname);
        teacher.setJname(jname);
        teacher.setEmail(email);
        teacher.setPhoneNumber(phoneNumber);
        teacher.setGender(Teacher.gender.valueOf(gender));
        teacher.setImg(imgUrl);
        teacher.setAccount(accountOpt.get());
        return teacherRepository.save(teacher);

    }
    private boolean isDuplicate(String email, String phoneNumber) {
        return teacherRepository.findByEmail(email).isPresent() || teacherRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
