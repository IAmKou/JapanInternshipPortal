package com.example.jip.controller;

import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentServices studentServices;

    @PostMapping("/save")
    public ResponseEntity<String> saveStudent(
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Change this to match your input
            localDate = LocalDate.parse(dob, formatter);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd.");
        }
        Date date = Date.valueOf(localDate);

        studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, passport_img,account_id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/admin.html"))
                .build();

    }

}
