package com.example.jip.controller;

import com.example.jip.entity.StudentAssignment;
import com.example.jip.services.StudentAssignmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("student/assignment")
public class StudentAssignmentController {

    @Autowired
    StudentAssignmentServices studentAssignmentServices;

    @GetMapping("/list")
    public List<StudentAssignment> getAllStudentAssignments(){
        return studentAssignmentServices.getStudentAssignments();
    }
}
