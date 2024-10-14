package com.example.jip.services;

import com.example.jip.entity.Student;
import com.example.jip.repository.StudentReposiroty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServices {
    @Autowired
    private StudentReposiroty studentReposiroty;

    public Student createStudent(Student student) {
        return studentReposiroty.save(student);
    }
}
