package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public List<TeacherDTO> getAllTeacher(){
        return teacherRepository.findAll().stream()
                .map(teacher -> new TeacherDTO(
                        teacher.getId(),
                        teacher.getFullname(),
                        teacher.getJname(),
                        teacher.getImg()
                ))
                .collect(Collectors.toList());
    }
}
