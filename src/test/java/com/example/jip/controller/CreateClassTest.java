package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import com.example.jip.services.ClassServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ListRepository listRepository;

    @InjectMocks
    private ClassServices classService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveClassWithStudents_ShouldSaveClassAndStudentsSuccessfully() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setFullname("John Doe");

        Semester semester = new Semester();
        semester.setId(1);
        semester.setName("Spring 2025");

        List<Integer> studentIds = List.of(1, 2, 3);
        List<Student> students = new ArrayList<>();
        for (int id : studentIds) {
            Student student = new Student();
            student.setId(id);
            students.add(student);
        }

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1);
        teacherDTO.setFullname("John Doe");

        ClassDTO classDTO = new ClassDTO();
        classDTO.setName("Math Class");
        classDTO.setTeacher(teacherDTO); // Properly set the TeacherDTO object
        classDTO.setSemesterId(semester.getId());

        Class newClass = new Class();
        newClass.setId(1);
        newClass.setName(classDTO.getName());
        newClass.setTeacher(teacher);
        newClass.setSemester(semester);
        newClass.setNumber_of_student(studentIds.size());
        newClass.setStatus(Class.status.Inactive);

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(semesterRepository.findById(semester.getId())).thenReturn(Optional.of(semester));
        when(classRepository.save(any(Class.class))).thenReturn(newClass);

        for (Student student : students) {
            when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        }

        // Act
        Class savedClass = classService.saveClassWithStudents(classDTO, studentIds, semester.getId());

        // Assert
        assertNotNull(savedClass);
        assertEquals(classDTO.getName(), savedClass.getName());
        assertEquals(studentIds.size(), savedClass.getNumber_of_student());
        assertEquals(teacher.getFullname(), savedClass.getTeacher().getFullname());
        assertEquals(semester.getId(), savedClass.getSemester().getId());

        verify(teacherRepository, times(1)).findById(teacher.getId());
        verify(semesterRepository, times(1)).findById(semester.getId());
        verify(classRepository, times(1)).save(any(Class.class));
        verify(studentRepository, times(studentIds.size())).findById(anyInt());
        verify(listRepository, times(studentIds.size())).save(any(Listt.class));
    }

    @Test
    void saveClassWithStudents_ShouldThrowExceptionWhenStudentListIsEmpty() {
        // Arrange
        ClassDTO classDTO = new ClassDTO();
        classDTO.setName("Science Class");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> classService.saveClassWithStudents(classDTO, new ArrayList<>(), 1));

        assertEquals("Student list cannot be empty.", exception.getMessage());
    }

    @Test
    void saveClassWithStudents_ShouldThrowExceptionWhenTeacherNotFound() {
        // Arrange
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(10);
        teacherDTO.setFullname("Unknown Teacher");

        ClassDTO classDTO = new ClassDTO();
        classDTO.setName("History Class");
        classDTO.setTeacher(teacherDTO);

        when(teacherRepository.findById(10)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classService.saveClassWithStudents(classDTO, List.of(1, 2, 3), 1));

        assertEquals("Teacher not found with ID: 10", exception.getMessage());
    }


    @Test
    void saveClassWithStudents_ShouldThrowExceptionWhenSemesterNotFound() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setFullname("John Doe");

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1);
        teacherDTO.setFullname("John Doe");

        ClassDTO classDTO = new ClassDTO();
        classDTO.setName("Biology Class");
        classDTO.setTeacher(teacherDTO);
        classDTO.setSemesterId(10); // Explicitly set the semesterId

        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher)); // Mock valid teacher
        when(semesterRepository.findById(10)).thenReturn(Optional.empty()); // Mock missing semester

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classService.saveClassWithStudents(classDTO, List.of(1, 2, 3), 10));

        assertEquals("Semester not found with ID: 10", exception.getMessage());
    }


}
