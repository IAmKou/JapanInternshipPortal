package com.example.jip.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import com.example.jip.dto.ClassDTO;
import com.example.jip.services.ClassServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

public class CreateClassTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ListRepository listRepository;

    @InjectMocks
    private ClassServices classService;

    private Teacher teacher;
    private Student student;
    private ClassDTO classDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock teacher
        teacher = new Teacher();
        teacher.setId(1);
        teacher.setFullname("John Doe");

        // Set up mock student
        student = new Student();
        student.setId(1);
        student.setFullname("Student 1");

        // Set up mock ClassDTO
        classDTO = new ClassDTO();
        classDTO.setName("Mathematics");
        classDTO.setTeacher(new TeacherDTO(teacher.getId(), teacher.getFullname()));
    }

    @Test
    void testSaveClassWithStudents_EmptyStudentList() {
        // Prepare an empty student list
        List<Integer> emptyStudentIds = new ArrayList<>();

        // Call the method under test and verify it throws an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                classService.saveClassWithStudents(classDTO, emptyStudentIds)
        );

        assertEquals("Student list cannot be empty.", exception.getMessage());
    }

    @Test
    void testSaveClassWithStudents_TeacherNotFound() {
        // Prepare mock behavior
        when(teacherRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Prepare the student IDs list
        List<Integer> studentIds = Collections.singletonList(student.getId());

        // Call the method under test and verify it throws an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                classService.saveClassWithStudents(classDTO, studentIds)
        );

        assertEquals("Teacher not found with ID: " + classDTO.getTeacher().getId(), exception.getMessage());
    }


}