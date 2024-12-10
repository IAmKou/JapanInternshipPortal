package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.AssignmentServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssignmentControllerTest {

    @InjectMocks
    private AssignmentController assignmentController;

    @Mock
    private AssignmentServices assignmentServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private TeacherRepository teacherRepository;

    @Test
    void testCreateAssignment_Success() throws Exception {
        // Arrange
        int teacherId = 1;
        AssignmentCreationRequest request = new AssignmentCreationRequest();

        // Mocking the request
        request.setDescription("homework");
        request.setClassIds(null); // Class IDs are null
        request.setCreated_date(new Date());
        request.setEnd_date(new SimpleDateFormat("yyyy-MM-dd").parse("2024-11-14")); // End date is 2024-12-14
        request.setContent("content"); // Assignment content
        request.setImgFile(null); // No files attached


        // Mock Teacher
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        Optional<Teacher> teacherOpt = Optional.of(teacher);
        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(teacherOpt);
        request.setTeacher(new TeacherDTO());
        // Act
        ResponseEntity<?> response = assignmentController.createAssignment(request, teacherId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Expecting 201 CREATED
        verify(assignmentServices, times(1)).createAssignment(request, teacherId); // Verify the service method is called once
    }

    @Test
    void testCreateAssignment_TeacherNotFound() {
        // Arrange
        int teacherId = 1;
        AssignmentCreationRequest request = new AssignmentCreationRequest();

        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assignmentController.createAssignment(request, teacherId));

        assertEquals("Failed to create assignment", exception.getMessage());
        verify(assignmentServices, never()).createAssignment(any(), teacherId);
    }

    @Test
    void testCreateAssignment_ServiceException() {
        // Arrange
        int teacherId = 1;
        AssignmentCreationRequest request = new AssignmentCreationRequest();

        // Mocking files
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("file1.png");
        when(file2.getOriginalFilename()).thenReturn("file2.jpg");
        request.setImgFile(new MultipartFile[]{file1, file2});
        request.setClassIds(List.of(1, 2, 3));

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        Optional<Teacher> teacherOpt = Optional.of(teacher);
        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(teacherOpt);

        doThrow(new RuntimeException("Service error")).when(assignmentServices).createAssignment(request, teacherId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assignmentController.createAssignment(request, teacherId));

        assertEquals("Failed to create assignment", exception.getMessage());
        verify(assignmentServices, times(1)).createAssignment(request, teacherId);
    }

    @Test
    void testGetAllAssignments_Success() {
        int teacherId = 1;

        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1);
        mockResponse.setDescription("Test Assignment");

        when(assignmentServices.getAllAssignmentByTeacherId(teacherId))
                .thenReturn(List.of(mockResponse));

        ResponseEntity<List<AssignmentResponse>> response = assignmentController.getAllAssignments(teacherId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Assignment", response.getBody().get(0).getDescription());

        verify(assignmentServices, times(1)).getAllAssignmentByTeacherId(teacherId);
    }

    @Test
    void testGetAllAssignments_NotFound() {
        int teacherId = 1;

        // Mock service để trả về danh sách rỗng
        when(assignmentServices.getAllAssignmentByTeacherId(teacherId))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AssignmentResponse>> response = assignmentController.getAllAssignments(teacherId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testDeleteAssignment_Success() {
        int assignmentId = 1;

        doNothing().when(assignmentServices).deleteAssignmentById(assignmentId);

        ResponseEntity<?> response = assignmentController.deleteAssignment(assignmentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(assignmentServices, times(1)).deleteAssignmentById(assignmentId);
    }

    @Test
    void testDeleteAssignment_NotFound() {
        int assignmentId = 1;

        doThrow(new RuntimeException("Assignment not found"))
                .when(assignmentServices).deleteAssignmentById(assignmentId);

        ResponseEntity<?> response = assignmentController.deleteAssignment(assignmentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Assignment not found with ID: 1", response.getBody());

        verify(assignmentServices, times(1)).deleteAssignmentById(assignmentId);
    }

    @Test
    void testGetAssignmentsForStudent_Success() {
        int studentId = 1;

        AssignmentResponse mockResponse = new AssignmentResponse();
        mockResponse.setId(1);
        mockResponse.setDescription("Test Assignment");

        when(assignmentServices.getAssignmentsForStudent(studentId))
                .thenReturn(List.of(mockResponse));

        ResponseEntity<List<AssignmentResponse>> response = assignmentController.getAssignmentsForStudent(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Assignment", response.getBody().get(0).getDescription());

        verify(assignmentServices, times(1)).getAssignmentsForStudent(studentId);
    }

    @Test
    void testGetAssignmentsForStudent_InternalError() {
        int studentId = 1;

        when(assignmentServices.getAssignmentsForStudent(studentId))
                .thenThrow(new RuntimeException("Unexpected Error"));

        ResponseEntity<List<AssignmentResponse>> response = assignmentController.getAssignmentsForStudent(studentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());

        verify(assignmentServices, times(1)).getAssignmentsForStudent(studentId);
    }
}
