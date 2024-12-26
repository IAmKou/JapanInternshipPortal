package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.MarkReport;
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

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private MarkReportRepository markReportRepository;


    public Student createStudent(String fullname, String japanname, Date dob, String gender, String phoneNumber,
                                 String email, MultipartFile img, MultipartFile passport, int accountId, String plainPassword) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + accountId);
        }

        // Check for duplicate email or phone number
        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        // Upload image and passport
        String imgUrl = cloudinaryService.uploadFileToFolder(img, "Account/").getUrl();
        String passUrl = cloudinaryService.uploadFileToFolder(passport, "Account/").getUrl();

        // Create a new Student object
        Student student = new Student();
        student.setFullname(fullname);
        student.setJapanname(japanname);
        student.setDob(dob);

        try {
            student.setGender(Student.Gender.valueOf(gender));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value: " + gender);
        }

        student.setPhoneNumber(phoneNumber);
        student.setEmail(email);
        student.setImg(imgUrl);
        student.setPassport(passUrl);
        student.setAccount(accountOpt.get());
        student.setMark(false);
        

        // Save the student to the database
        Student savedStudent = studentRepository.save(student);

        MarkReport markReport = new MarkReport();
        markReport.setStudent(savedStudent);
        markReportRepository.save(markReport);

        // Send email with plain-text password
        String emailStatus = emailServices.sendEmail(email, accountOpt.get().getUsername(), plainPassword);
        if (emailStatus == null) {
            System.out.println("Failed to send email to: " + email);
        } else {
            System.out.println("Email sent successfully to: " + email);
        }

        return savedStudent;
    }

    public int getStudentIdByAccountId(int accountId) {
        return studentRepository.findStudentIdByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("No student found for the given account ID"));
    }

    private boolean isDuplicate(String email, String phoneNumber) {
        return studentRepository.findByEmail(email).isPresent() || studentRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}





