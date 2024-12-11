package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.exam.ExamCreationRequest;
import com.example.jip.dto.request.exam.ExamUpdateRequest;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ExamSerivce;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamControllerTest {

    @InjectMocks
    private ExamController examController;

    @Mock
    private ExamSerivce examService;

    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExam_Success() {
        // Arrange
        int teacherId = 1;
        ExamCreationRequest request = new ExamCreationRequest();
        Teacher teacher = new Teacher();
        teacher.setId(1);

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacher.getId());
        request.setTeacher(teacherDTO);

        Mockito.when(teacherRepository.findByAccount_id(teacherId)).thenReturn(Optional.of(teacher));


        // Act
        ResponseEntity<?> response = examController.createExam(request, teacherId);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


    @Test
    void testCreateExam_TeacherNotFound() {
        int teacherId = 1;
        ExamCreationRequest request = new ExamCreationRequest();
        request.setExam_name(null);

        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = examController.createExam(request, teacherId);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Teacher not found for account ID: 1", response.getBody());
        verify(teacherRepository, times(1)).findByAccount_id(teacherId);
        verify(examService, never()).createExam(any());
    }

    @Test
    void testUpdateExam_Success() {
        int examId = 1;
        ExamUpdateRequest request = new ExamUpdateRequest();
        request.setExam_name("Updated exam name");
        request.setContent(null);
        request.setExam_date(null);

        doNothing().when(examService).updateAssignment(examId, request);

        ResponseEntity<?> response = examController.updateExam(examId, request);

        assertEquals(204, response.getStatusCodeValue());
        verify(examService, times(1)).updateAssignment(examId, request);
    }

    @Test
    void testUpdateExam_NotFound() {
        int examId = 1;
        ExamUpdateRequest request = new ExamUpdateRequest();
        request.setExam_name("Updated exam name");

        doThrow(new NoSuchElementException("Not found")).when(examService).updateAssignment(examId, request);

        ResponseEntity<?> response = examController.updateExam(examId, request);

        assertEquals(404, response.getStatusCodeValue());
        verify(examService, times(1)).updateAssignment(examId, request);
    }

}
