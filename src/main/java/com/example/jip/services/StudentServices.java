package com.example.jip.services;

import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.Account;
import com.example.jip.entity.Student;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.sql.Date;
import java.util.Optional;

    @Service
    public class StudentServices {
        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private CloudinaryService cloudinaryService;

        public Student createStudent(String fullname, String japanname, Date dob, String gender, String phoneNumber, String email, MultipartFile img, MultipartFile passport, int accountId) {
            // Check if the account exists
            Optional<Account> accountOpt = accountRepository.findById(accountId);
            if (!accountOpt.isPresent()) {
                throw new IllegalArgumentException("No account found with id: " + accountId);
            }

            CloudinaryResponse response = cloudinaryService.uploadFileToFolder(img, "Account/" );
            String imgUrl =  response.getUrl();
            CloudinaryResponse responses = cloudinaryService.uploadFileToFolder(passport, "Account/" );
            String passUrl =  response.getUrl();

            // Create a new Student object and set its properties
            Student student = new Student();
            student.setFullname(fullname);
            student.setJapanname(japanname);
            student.setDob(dob);
            student.setGender(Student.Gender.valueOf(gender));
            student.setPhoneNumber(phoneNumber);
            student.setEmail(email);
            student.setImg(imgUrl);
            student.setAccount(accountOpt.get());
            student.setMark(false);

            // Save the student to the database
            return studentRepository.save(student);
        }

        public int getStudentIdByAccountId(int accountId) {
            return studentRepository.findStudentIdByAccountId(accountId)
                    .orElseThrow(() -> new RuntimeException("No student found for the given account ID"));
        }
    }



