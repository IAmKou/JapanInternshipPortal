package com.example.jip.services;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;  // Assuming you have this repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassServices {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public Class saveClassWithStudents(ClassDTO classDTO, List<Integer> studentIds) {
        // Check if the studentIds list is empty
        if (studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("Student list cannot be empty.");
        }

        Teacher teacher = teacherRepository.findById(classDTO.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + classDTO.getTeacher().getId()));

        Class newClass = new Class();
        newClass.setName(classDTO.getName());
        newClass.setTeacher(teacher);
        newClass.setNumber_of_student(studentIds.size());

        Class savedClass = classRepository.save(newClass);

        for (Integer studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

            Listt listEntry = new Listt();
            ListId listId = new ListId(savedClass.getId(), student.getId());
            listEntry.setId(listId);
            listEntry.setClas(savedClass);
            listEntry.setStudent(student);

            listRepository.save(listEntry);
        }

        return savedClass;
    }


    public ClassDTO updateClass (int id, ClassDTO classDTO) {
        Class newClass = classRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id " + id));
        if (classDTO.getName() != null && !classDTO.getName().isEmpty()) {
            newClass.setName(classDTO.getName());
        }

        if (classDTO.getTeacher() != null) {
            Teacher newTeacher = teacherRepository.findById(classDTO.getTeacher().getId())
                    .orElseThrow(() -> new NoSuchElementException("Teacher not found with id " + classDTO.getTeacher().getId()));
            newClass.setTeacher(newTeacher);
        }

        classRepository.save(newClass);


        return new ClassDTO(newClass);
    }
}
