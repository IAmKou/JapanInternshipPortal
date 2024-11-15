package com.example.jip.controller;

import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.Student;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/student")

public class StudentController {
    @Autowired
    StudentServices studentServices;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/save")
    public RedirectView saveStudent(
            @RequestParam String fullname
            , @RequestParam String japanname
            , @RequestParam String dob
            , @RequestParam String gender
            , @RequestParam String email
            , @RequestParam String phoneNumber
            , @RequestParam(required = false) String img
            , @RequestParam(required = false) String passport_img
            , @RequestParam int account_id) {

        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            localDate = LocalDate.parse(dob, formatter);
        } catch (DateTimeParseException e) {
            RedirectView redirectView = new RedirectView("/error");
            redirectView.addStaticAttribute("message", "Invalid date format. Use yyyy-MM-dd.");
            return redirectView;
        }


        Date date = Date.valueOf(localDate);
        studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, passport_img, account_id);
        return new RedirectView("/student.html");
    }

    @GetMapping("/get")
    public List<StudentDTO> getTopStudents() {
        return studentRepository.findTop30UnassignedStudents();
    }


}
