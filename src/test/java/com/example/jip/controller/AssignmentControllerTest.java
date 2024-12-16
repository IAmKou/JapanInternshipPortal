package com.example.jip.controller;

import com.example.jip.configuration.Config;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AssignmentClassRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.AssignmentServices;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ContextConfiguration(classes = Config.class)
@WebMvcTest(AssignmentController.class)
public class AssignmentControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AssignmentControllerTest.class);
    @InjectMocks
    private AssignmentController assignmentController;

    @MockBean
    private AssignmentServices assignmentServices;

    @MockBean
    ClassRepository classRepository;

    @MockBean
    TeacherRepository teacherRepository;

    @MockBean
    AssignmentClassRepository assignmentClassRepository;

    @Autowired
    ObjectMapper ObjectMapper;

    @Autowired
    MockMvc mockMvc;

    private final String END_POINT_PATH = "/assignment";

    private AssignmentCreationRequest mockAssignmentCreationRequest() {
        // Set created_date to 2024-12-10
        Calendar createdCalendar = Calendar.getInstance();
        createdCalendar.set(2024, Calendar.DECEMBER, 10, 0, 0, 0);
        createdCalendar.set(Calendar.MILLISECOND, 0);
        Date create_date = createdCalendar.getTime();

        // Set end_date to 2024-11-14
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(2024, Calendar.NOVEMBER, 14, 0, 0, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        Date end_date = endCalendar.getTime();



        MultipartFile[] imgFiles = new MultipartFile[0]; // Empty array to avoid null errors
        List<Integer> classIds = new ArrayList<>(); // Initialize empty list

        return new AssignmentCreationRequest(
                create_date,       // Created date
                end_date,          // End date
                "homework", // Mock description
                "content", // Mock content
                imgFiles,          // Empty file array
                null,        // Teacher DTO
                classIds           // Empty class IDs
        );
    }

    @Test
    void testCreateAssignment_Return201Exception() throws Exception {
        // Mock Teacher Entity
        Teacher mockTeacher = new Teacher();
        mockTeacher.setId(1);
        Mockito.when(teacherRepository.findByAccount_id(1)).thenReturn(Optional.of(mockTeacher));

        // Prepare Date Format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Parse Dates
        Date createdDate = dateFormat.parse("2024-12-10");
        Date endDate = dateFormat.parse("2024-12-14");

        // Prepare Request Data
        String description = "homework";
        String content = "content";

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "/create")
                        .file("imgFile", new byte[0])
                        .param("description", description)
                        .param("created_date", dateFormat.format(createdDate))
                        .param("end_date", dateFormat.format(endDate))
                        .param("content", content)
                        .param("teacher_id", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // Verify Service Method Call
        Mockito.verify(assignmentServices, Mockito.times(1)).createAssignment(any(AssignmentCreationRequest.class));
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
        verify(assignmentServices, never()).createAssignment(any());
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

        doThrow(new RuntimeException("Service error")).when(assignmentServices).createAssignment(request);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> assignmentController.createAssignment(request, teacherId));

        assertEquals("Failed to create assignment", exception.getMessage());
        verify(assignmentServices, times(1)).createAssignment(request);
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
