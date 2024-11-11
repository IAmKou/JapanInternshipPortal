package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.TeacherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.jip.services.ClassServices;
import com.example.jip.entity.Class;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassServices classServices;

    @PostMapping("/create")
    public String createClass(@RequestParam String className, @RequestParam int teacherId,  @RequestBody List<Integer> studentIds) {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setName(className);
        classDTO.setNumberOfStudents(studentIds.size());

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacherId);
        classDTO.setTeacher(teacherDTO);

        Class savedClass = classServices.saveClassWithStudents(classDTO, studentIds);
        return "Class " + savedClass.getName() + " created with ID: " + savedClass.getId();



    }

}
