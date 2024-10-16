package com.example.jip.controller;

import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentServices studentServices;

    @PostMapping("/save")
    public ResponseEntity<String> saveStudent(@RequestParam int accountId
            , @RequestParam String fullname
            , @RequestParam String japanname
            , @RequestParam String dob
            , @RequestParam String gender
            , @RequestParam String email
            , @RequestParam String phoneNumber) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try{
            date = new Date(dateFormat.parse(dob).getTime());
        }catch(ParseException e){
            e.printStackTrace();
        }

        studentServices.createStudent(accountId, fullname, japanname, date, gender, phoneNumber, email);
        return ResponseEntity.ok("Student information saved successfully");

    }

}
