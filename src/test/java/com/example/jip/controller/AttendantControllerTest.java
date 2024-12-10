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
import java.util.ArrayList;
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
        // Prepare test data
        int classId = 1;
        List<AttendantDTO> attendanceData = new ArrayList<>();
        AttendantDTO dto = new AttendantDTO();
        dto.setStudentId(101);
        dto.setStatus(Attendant.Status.Present);
        dto.setDate(Date.valueOf("2024-12-10"));
        attendanceData.add(dto);

        // No exception thrown during service call
        doNothing().when(attendantServices).updateAttendance(classId, attendanceData);

        // Perform the test
        ResponseEntity<String> response = attendantController.updateAttendance(classId, attendanceData);

        // Verify interactions
        verify(attendantServices, times(1)).updateAttendance(classId, attendanceData);

        // Assert the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Attendance updated successfully!", response.getBody());
    }

    @Test
    void testUpdateAttendance_Failure() {
        // Prepare test data
        int classId = 1;
        List<AttendantDTO> attendanceData = new ArrayList<>();
        AttendantDTO dto = new AttendantDTO();
        dto.setStudentId(101);
        dto.setStatus(Attendant.Status.Present);
        dto.setDate(Date.valueOf("2024-12-10"));
        attendanceData.add(dto);

        // Simulate an exception during service call
        doThrow(new RuntimeException("Service error")).when(attendantServices).updateAttendance(classId, attendanceData);

        // Perform the test
        ResponseEntity<String> response = attendantController.updateAttendance(classId, attendanceData);

        // Verify interactions
        verify(attendantServices, times(1)).updateAttendance(classId, attendanceData);

        // Assert the response
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to update attendance: Service error", response.getBody());
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

    @Test
    void testSaveAttendance_Success() {
        // Prepare test data
        int classId = 1;
        Date attendanceDate = Date.valueOf("2024-12-10");
        LocalTime currentLocalTime = LocalTime.of(9, 30);
        Time startTime = Time.valueOf("09:00:00");
        Time endTime = Time.valueOf("10:00:00");

        // Mock the schedules
        Schedule mockSchedule = new Schedule();
        mockSchedule.setId(1);
        mockSchedule.setStart_time(startTime);
        mockSchedule.setEnd_time(endTime);
        when(scheduleRepository.findByClassIdAndDate(classId, attendanceDate)).thenReturn(Collections.singletonList(mockSchedule));

        // Mock current time
        try (MockedStatic<LocalTime> localTimeMock = mockStatic(LocalTime.class)) {
            localTimeMock.when(LocalTime::now).thenReturn(currentLocalTime);

            // Prepare DTO
            AttendantDTO request = new AttendantDTO();
            request.setStudentId(101);
            request.setStatus(Attendant.Status.Present);
            request.setDate(attendanceDate);

            List<AttendantDTO> attendanceRequests = Collections.singletonList(request);

            // Perform the test
            ResponseEntity<String> response = attendantController.save(attendanceRequests, classId);

            // Verify calls
            verify(scheduleRepository, times(1)).findByClassIdAndDate(classId, attendanceDate);
            verify(attendantServices, times(1)).createAttendant(
                    request.getStudentId(),
                    mockSchedule.getId(),
                    request.getStatus().toString(),
                    attendanceDate,
                    classId
            );

            // Assert the response
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("Attendance saved successfully.", response.getBody());
        }
    }
    @Test
    void testSaveAttendance_DateMissing() {
        // Prepare test data
        int classId = 1;

        // Prepare DTO with missing date
        AttendantDTO request = new AttendantDTO();
        request.setStudentId(101);
        request.setStatus(Attendant.Status.Present);

        List<AttendantDTO> attendanceRequests = Collections.singletonList(request);

        // Perform the test
        ResponseEntity<String> response = attendantController.save(attendanceRequests, classId);

        // Verify no interactions with repositories
        verifyNoInteractions(scheduleRepository);
        verifyNoInteractions(attendantServices);

        // Assert the response
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Date is missing in the request.", response.getBody());
    }

    @Test
    void testSaveAttendance_TimeMismatch() {
        // Prepare test data
        int classId = 1;
        Date attendanceDate = Date.valueOf("2024-12-10");
        LocalTime currentLocalTime = LocalTime.of(9, 30); // Time not within the schedule
        Time startTime = Time.valueOf("09:00:00");
        Time endTime = Time.valueOf("10:00:00");

        // Mock the schedules
        Schedule mockSchedule = new Schedule();
        mockSchedule.setId(1);
        mockSchedule.setStart_time(startTime);
        mockSchedule.setEnd_time(endTime);
        when(scheduleRepository.findByClassIdAndDate(classId, attendanceDate)).thenReturn(Collections.singletonList(mockSchedule));

        // Mock current time
        try (MockedStatic<LocalTime> localTimeMock = mockStatic(LocalTime.class)) {
            localTimeMock.when(LocalTime::now).thenReturn(currentLocalTime);

            // Prepare DTO
            AttendantDTO request = new AttendantDTO();
            request.setStudentId(101);
            request.setStatus(Attendant.Status.Present);
            request.setDate(attendanceDate);

            List<AttendantDTO> attendanceRequests = Collections.singletonList(request);

            // Perform the test
            ResponseEntity<String> response = attendantController.save(attendanceRequests, classId);

            // Verify calls
            verify(scheduleRepository, times(1)).findByClassIdAndDate(classId, attendanceDate);
            verifyNoInteractions(attendantServices);

            // Assert the response
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Attendance cannot be saved as the time does not match any schedule slot.", response.getBody());
        }
    }
}
