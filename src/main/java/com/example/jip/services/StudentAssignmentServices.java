package com.example.jip.services;

import com.example.jip.entity.StudentAssignment;
import com.example.jip.repository.StudentAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAssignmentServices {

    @Autowired
    StudentAssignmentRepository studentAssignmentRepository;

    public List<StudentAssignment> getStudentAssignments(){
        return studentAssignmentRepository.findAll();
    }



}
