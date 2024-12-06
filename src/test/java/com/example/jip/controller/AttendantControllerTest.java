package com.example.jip.controller;

import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.AttendantServices;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendantControllerTest {

    @InjectMocks
    private AttendantController attendantController;

    @Mock
    private AttendantServices attendantServices;

    @Mock
    private AttendantRepository attendantRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    public AttendantControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAttendance_NoScheduleFound() {
        // Arrange
        int classId = 1;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        AttendantDTO request = new AttendantDTO();
        request.setStudentId(1);
        request.setStatus(Attendant.Status.Present);
        request.setDate(date);

        when(scheduleRepository.findByClassIdAndDate(classId, date))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<String> response = attendantController.save(List.of(request), classId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No schedule found for the given class and date.", response.getBody());
        verify(attendantServices, never()).createAttendant(anyInt(), anyInt(), anyString(), any(Date.class), anyInt());
    }

    @Test
    void testGetAttendance_Success() {
        // Arrange
        int classId = 1;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        AttendantDTO mockAttendance = new AttendantDTO();
        mockAttendance.setStudentId(1);
        mockAttendance.setStatus(Attendant.Status.Present);
        mockAttendance.setDate(date);

        when(attendantRepository.findByDateAndClassId(date, classId))
                .thenReturn(List.of(mockAttendance));

        // Act
        List<AttendantDTO> response = attendantController.getAttendance(classId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1, response.get(0).getStudentId());
        verify(attendantRepository, times(1)).findByDateAndClassId(date, classId);
    }

    @Test
    void testUpdateAttendance_Success() {
        // Arrange
        int classId = 1;
        AttendantDTO mockAttendance = new AttendantDTO();
        mockAttendance.setStudentId(1);
        mockAttendance.setStatus(Attendant.Status.Present);

        // Act
        ResponseEntity<String> response = attendantController.updateAttendance(classId, List.of(mockAttendance));

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Attendance updated successfully!", response.getBody());
        verify(attendantServices, times(1)).updateAttendance(eq(classId), anyList());
    }

    @Test
    void testHasAttendanceBeenTaken_AttendanceExists() {
        // Arrange
        int classId = 1;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        when(attendantRepository.existsByClassIdAndDate(classId, date)).thenReturn(true);

        // Act
        boolean response = attendantController.hasAttendanceBeenTaken(classId);

        // Assert
        assertTrue(response);
        verify(attendantRepository, times(1)).existsByClassIdAndDate(classId, date);
    }

    @Test
    void testHasAttendanceBeenTaken_NoAttendance() {
        // Arrange
        int classId = 1;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        when(attendantRepository.existsByClassIdAndDate(classId, date)).thenReturn(false);

        // Act
        boolean response = attendantController.hasAttendanceBeenTaken(classId);

        // Assert
        assertFalse(response);
        verify(attendantRepository, times(1)).existsByClassIdAndDate(classId, date);
    }
}
