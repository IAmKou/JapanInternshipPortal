package com.example.jip.services;

import com.example.jip.entity.Student;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class StudentServices {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Student createStudent(int accountId, String fullname, String japanname, Date dob, String gender, String phoneNumber, String email) {
        Student student = new Student();
        return studentRepository.save(student);
    }
}
