package com.example.jip.services;

import com.example.jip.entity.*;
import com.example.jip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServices {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AccountRepository accountRepository;



    @Autowired
    private S3Service s3Service;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private MarkReportRepository markReportRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private MarkRpExamRepository markRpExamRepository;

    public Student createStudent(String fullname, String japanname, Date dob, String gender, String phoneNumber, String email, MultipartFile img, MultipartFile passport, int accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + accountId);
        }

        // Check for duplicate email or phone number
        if (isDuplicate(email, phoneNumber)) {
            throw new IllegalArgumentException("Duplicate email or phone number found");
        }

        String folderName = sanitizeFolderName("Account/Student/" + accountOpt.get().getUsername());
        String folderNameP = sanitizeFolderName("Account/Student/" + accountOpt.get().getUsername() + "/Passport");

        // Upload image and passport
        String imgUrl = s3Service.uploadFile(img, folderName, img.getOriginalFilename());
        String passUrl =  s3Service.uploadFile(passport, folderNameP, img.getOriginalFilename());

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

        List<Exam> examList = examRepository.findAll();
        if (examList.isEmpty()) {
            throw new IllegalStateException("No exams exist in the database.");
        }

        for (Exam exam : examList) {
            MarkReportExam markRpExam = new MarkReportExam(markReport, exam);
            markRpExamRepository.save(markRpExam);
        }

        String account = accountOpt.get().getUsername();
        String password = accountOpt.get().getPassword();
        String emailStatus = emailServices.sendEmail(email, password, account);
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

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }
}





