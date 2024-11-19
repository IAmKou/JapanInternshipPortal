package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.jip.services.ClassServices;
import com.example.jip.entity.Class;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassServices classServices;

    @Autowired
    private ClassRepository classRepository;

    @PostMapping("/create")
    public String createClass(@RequestBody ClassDTO classDTO) {
        if (classDTO.getName() == null || classDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Class name is required");
        }
        if (classDTO.getTeacher() == null || classDTO.getTeacher().getId() == 0) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        int classCount = classRepository.countByTeacherId(classDTO.getTeacher().getId());
        if (classCount >= 3) {
            return "This teacher already has the maximum number of classes (3).";
        }


        Class savedClass = classServices.saveClassWithStudents(classDTO, classDTO.getStudentIds());
        return "Class " + savedClass.getName() + " created successfully";
    }

    @GetMapping("/get")
    public List<ClassDTO> getClasses() {
        return classRepository.findAll().stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteClass(@PathVariable int id) {
        if (classRepository.existsById(id)) {
            classRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
