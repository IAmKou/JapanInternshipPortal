package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeacherServices {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AccountRepository accountRepository;


    public Teacher createTeacher(String fullname, String jname, String email, String phoneNumber, String gender, String img, int account_id) {
        Optional<Account> accountOpt = accountRepository.findById(account_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + account_id);
        }

        Teacher teacher = new Teacher();
        teacher.setFullname(fullname);
        teacher.setJname(jname);
        teacher.setEmail(email);
        teacher.setPhoneNumber(phoneNumber);
        teacher.setGender(Teacher.gender.valueOf(gender));
        teacher.setImg(img);
        teacher.setAccount_id(account_id);
        return teacherRepository.save(teacher);

    }
}
