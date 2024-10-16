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

    public Student createStudent(int id, String fullname, String japanname, Date dob, String gender, String phoneNumber, String email, String img, String passport) {
        // Create a new Student object and set its properties
        Student student = new Student();
        student.setId(id);
        student.setFullname(fullname);
        student.setJapanname(japanname);
        student.setDob(dob);
        student.setGender(Student.Gender.valueOf(gender));
        student.setPhoneNumber(phoneNumber);
        student.setEmail(email);
        student.setImg(img);
        student.setPassport(passport);

        // Save the student to the database
        return studentRepository.save(student);
    }

}
