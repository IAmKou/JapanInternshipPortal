package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.jip.services.ClassServices;
import com.example.jip.entity.Class;


@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassServices classServices;

    @PostMapping("/create")
    public String createClass(@RequestBody ClassDTO classDTO) {
        if (classDTO.getName() == null || classDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Class name is required");
        }
        if (classDTO.getTeacher() == null || classDTO.getTeacher().getId() == 0) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        Class savedClass = classServices.saveClassWithStudents(classDTO, classDTO.getStudentIds());
        return "Class " + savedClass.getName() + " created with ID: " + savedClass.getId();
    }


}
