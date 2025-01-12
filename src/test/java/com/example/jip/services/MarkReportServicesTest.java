package com.example.jip.services;

import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.MarkReport;
import com.example.jip.entity.Student;
import com.example.jip.entity.StudentAssignment;
import com.example.jip.repository.AssignmentStudentRepository;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.MarkReportRepository;
import com.example.jip.repository.StudentAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarkReportServiceTest {

    @Mock
    private MarkReportRepository markReportRepository;

    @Mock
    private AssignmentStudentRepository assignmentStudentRepository;

    @Mock
    private StudentAssignmentRepository studentAssignmentRepository;

    @Mock
    private AttendantRepository attendantRepository;

    @InjectMocks
    private MarkReportServices markReportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getListMarkReport_ShouldReturnMarkReports() {
        // Arrange
        int classId = 1;

        // Mock MarkReport
        MarkReport mockMarkReport = new MarkReport();
        mockMarkReport.setId(1);

        Student student = new Student();
        student.setId(1);
        student.setFullname("John Doe");
        student.setEmail("johndoe@example.com");
        mockMarkReport.setStudent(student);
        mockMarkReport.setSoftskill(new BigDecimal("8.0"));
        mockMarkReport.setSkill(new BigDecimal("7.0"));

        // Mock Repositories
        when(markReportRepository.findAllByClassId(classId)).thenReturn(List.of(mockMarkReport));
        when(assignmentStudentRepository.countByStudentId(1)).thenReturn(5);
        when(studentAssignmentRepository.countStudentAssignmentByStudentId(1)).thenReturn(5);
        when(studentAssignmentRepository.findAllByStudentId(1)).thenReturn(List.of(
                createStudentAssignment(1, new BigDecimal("8.0")),
                createStudentAssignment(1, new BigDecimal("9.0"))
        ));

        when(attendantRepository.countByStudentId(1)).thenReturn(10);
        when(attendantRepository.countByStudentIdAndStatus(1, Attendant.Status.PRESENT)).thenReturn(8);

        // Act
        List<MarkReportResponse> result = markReportService.getListMarkReport(classId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MarkReportResponse response = result.get(0);

        assertEquals("John Doe", response.getStudentName());
        assertEquals("johndoe@example.com", response.getStudentEmail());
        assertEquals(new BigDecimal("3.40"), response.getAssignment());
        assertEquals(new BigDecimal("8.0"), response.getSoftskill());
        assertEquals(new BigDecimal("7.0"), response.getSkill());
        assertEquals(new BigDecimal("8.00"), response.getAttendant());
        assertNotNull(response.getFinal_mark());
    }

    @Test
    void getListMarkReport_ShouldHandleNoMarkReports() {
        // Arrange
        int classId = 1;
        when(markReportRepository.findAllByClassId(classId)).thenReturn(List.of());

        // Act
        List<MarkReportResponse> result = markReportService.getListMarkReport(classId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getListMarkReport_ShouldHandleNullMarksInStudentAssignments() {
        // Arrange
        int classId = 1;

        // Mock MarkReport
        MarkReport mockMarkReport = new MarkReport();
        mockMarkReport.setId(1);

        Student student = new Student();
        student.setId(1);
        student.setFullname("John Doe");
        student.setEmail("johndoe@example.com");
        mockMarkReport.setStudent(student);

        // Mock Repositories
        when(markReportRepository.findAllByClassId(classId)).thenReturn(List.of(mockMarkReport));
        when(assignmentStudentRepository.countByStudentId(1)).thenReturn(5);
        when(studentAssignmentRepository.countStudentAssignmentByStudentId(1)).thenReturn(5);
        when(studentAssignmentRepository.findAllByStudentId(1)).thenReturn(List.of(
                createStudentAssignment(1, null) // Simulate null marks
        ));

        when(attendantRepository.countByStudentId(1)).thenReturn(10);
        when(attendantRepository.countByStudentIdAndStatus(1, Attendant.Status.PRESENT)).thenReturn(8);

        // Act
        List<MarkReportResponse> result = markReportService.getListMarkReport(classId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MarkReportResponse response = result.get(0);

        assertNull(response.getAssignment());
        assertNull(response.getFinal_mark());
    }

    private StudentAssignment createStudentAssignment(int studentId, BigDecimal mark) {
        Student student = new Student(); // Create a Student object
        student.setId(studentId); // Set the student ID

        StudentAssignment assignment = new StudentAssignment();
        assignment.setStudent(student); // Set the Student object
        assignment.setMark(mark); // Set the mark
        return assignment;
    }

}
