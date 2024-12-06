package com.example.jip.controller;

import com.example.jip.dto.ReportDTO;
import com.example.jip.entity.Report;
import com.example.jip.repository.ReportRepository;
import com.example.jip.services.ReportServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportServices reportServices;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReport_Success() {
        // Arrange
        String title = "Test Title";
        String content = "Test Content";
        int uid = 1;

        Report mockReport = new Report();
        mockReport.setTitle(title);
        mockReport.setContent(content);

        when(reportServices.createReport(title, content, uid)).thenReturn(mockReport);

        // Act
        ResponseEntity<String> response = reportController.createReport(title, content, uid);

        // Assert
        assertEquals("Report submitted successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(reportServices, times(1)).createReport(title, content, uid);
    }

    @Test
    void testCreateReport_Failure() {
        // Arrange
        String title = "Test Title";
        String content = "Test Content";
        int uid = 1;

        when(reportServices.createReport(title, content, uid))
                .thenThrow(new IllegalArgumentException("No account found with id: " + uid));

        // Act
        ResponseEntity<String> response = reportController.createReport(title, content, uid);

        // Assert
        assertEquals("Failed to create report: No account found with id: 1", response.getBody());
        assertEquals(500, response.getStatusCodeValue());
        verify(reportServices, times(1)).createReport(title, content, uid);
    }

}
