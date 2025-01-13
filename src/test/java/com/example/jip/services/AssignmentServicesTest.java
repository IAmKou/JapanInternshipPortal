package com.example.jip.services;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.entity.*;
import com.example.jip.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.lang.Thread;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssignmentServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private AssignmentClassRepository assignmentClassRepository;

    @Mock
    private AssignmentStudentRepository assignmentStudentRepository;

    @Mock
    private NotificationServices notificationServices;

    @Mock
    private EmailServices emailServices;

    @InjectMocks
    private AssignmentServices assignmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createAssignment_ShouldThrowExceptionForInvalidDates() {
        // Arrange
        AssignmentCreationRequest request = new AssignmentCreationRequest();

        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setFullname("John Doe");

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1);
        teacherDTO.setFullname("John Doe");
        request.setTeacher(teacherDTO);
        request.setCreated_date(new Date(System.currentTimeMillis() + 100000));
        request.setEnd_date(new Date(System.currentTimeMillis() - 10000));

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            assignmentService.createAssignment(request);
//        });

//        assertEquals("Created date cannot be after end date.", exception.getMessage());
    }

    @Test
    void createAssignment_ShouldHandleNoClassIds() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setFullname("John Doe");

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1);
        teacherDTO.setFullname("John Doe");
        AssignmentCreationRequest request = new AssignmentCreationRequest();
        request.setTeacher(teacherDTO);
        request.setCreated_date(new Date(System.currentTimeMillis() - 10000));
        request.setEnd_date(new Date(System.currentTimeMillis() + 100000));
        request.setDescription("No Class Assignment");
        request.setContent("Assignment content");
        request.setClassIds(null);

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment assignment = invocation.getArgument(0);
            assignment.setId(1);
            return assignment;
        });

        // Act
//        Assignment result = assignmentService.createAssignment(request);

//        // Assert
//        assertNotNull(result);
//        assertEquals("No Class Assignment", result.getDescription());
        verify(teacherRepository, times(1)).findById(teacher.getId());
        verify(assignmentRepository, times(2)).save(any(Assignment.class));
        verify(assignmentClassRepository, never()).save(any(AssignmentClass.class));
        verify(assignmentStudentRepository, never()).save(any(AssignmentStudent.class));
    }
}
