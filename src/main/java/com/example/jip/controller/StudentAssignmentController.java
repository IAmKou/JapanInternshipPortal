package com.example.jip.controller;

import com.example.jip.services.StudentAssignmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/student-assignment")
public class StudentAssignmentController {

    @Autowired
    StudentAssignmentServices studentAssignmentServices;

    @GetMapping("/list")
    public RedirectView getAllStudentAssignments(){
        studentAssignmentServices.getAllStudentAssignments();
        return new  RedirectView("/student-assignment.html");
    }
}
