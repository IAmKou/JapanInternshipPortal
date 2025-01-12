package com.example.jip.controller;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import com.example.jip.repository.*;
import com.example.jip.services.EmailServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleControllerTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AttendantRepository attendantRepository;

    @Mock
    private EmailServices emailServices;

    @InjectMocks
    private ScheduleController scheduleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteSchedule_ShouldDeleteSuccessfully() {
        // Arrange
        int scheduleId = 1;
        when(scheduleRepository.existsById(scheduleId)).thenReturn(true);

        // Act
        boolean result = scheduleController.deleteSchedule(scheduleId);

        // Assert
        assertTrue(result);
        verify(scheduleRepository, times(1)).deleteById(scheduleId);
    }

    @Test
    void deleteSchedule_ShouldReturnFalseIfScheduleNotFound() {
        // Arrange
        int scheduleId = 1;
        when(scheduleRepository.existsById(scheduleId)).thenReturn(false);

        // Act
        boolean result = scheduleController.deleteSchedule(scheduleId);

        // Assert
        assertFalse(result);
        verify(scheduleRepository, never()).deleteById(anyInt());
    }

    @Test
    void getSchedule_ShouldReturnScheduleList() {
        // Arrange
        int semesterId = 1;
        Schedule schedule = new Schedule();
        schedule.setId(1);
        schedule.setStatus(Schedule.status.Draft);
        schedule.setSemester(new Semester());

        when(scheduleRepository.findBySemesterIdAndStatus(semesterId, Schedule.status.Draft))
                .thenReturn(List.of(schedule));

        // Act
        List<ScheduleDTO> result = scheduleController.getSchedule(semesterId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository, times(1))
                .findBySemesterIdAndStatus(semesterId, Schedule.status.Draft);
    }

    @Test
    void saveDraft_ShouldSaveSuccessfully() {
        // Arrange
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setClassId(1);
        scheduleDTO.setSemesterId(1);
        scheduleDTO.setDate(LocalDate.now().toString());
        scheduleDTO.setActivity("Activity 1");

        com.example.jip.entity.Class clasz = new com.example.jip.entity.Class();
        clasz.setId(1);
        Semester semester = new Semester();
        semester.setId(1);

        when(classRepository.findById(1)).thenReturn(Optional.of(clasz));
        when(semesterRepository.findById(1)).thenReturn(Optional.of(semester));

        // Act
        ResponseEntity<?> response = scheduleController.saveDraft(List.of(scheduleDTO));

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void saveDraft_ShouldReturnBadRequestIfEmpty() {
        // Act
        ResponseEntity<?> response = scheduleController.saveDraft(new ArrayList<>());

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No schedules provided.", response.getBody());
    }

    @Test
    void savePublic_ShouldSaveSuccessfully() {
        // Arrange
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setClassId(1);
        scheduleDTO.setSemesterId(1);
        scheduleDTO.setDate(LocalDate.now().toString());
        scheduleDTO.setActivity("Activity 1");

        com.example.jip.entity.Class clasz = new com.example.jip.entity.Class();
        clasz.setId(1);
        Semester semester = new Semester();
        semester.setId(1);

        when(classRepository.findById(1)).thenReturn(Optional.of(clasz));
        when(semesterRepository.findById(1)).thenReturn(Optional.of(semester));

        // Act
        ResponseEntity<?> response = scheduleController.savePublic(List.of(scheduleDTO));

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void updateEvent_ShouldUpdateSuccessfully() {
        // Arrange
        int scheduleId = 1;
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setRoom("Room 101");
        scheduleDTO.setActivity("New Activity");
        scheduleDTO.setColor("#FFFFFF");

        Schedule existingSchedule = new Schedule();
        existingSchedule.setId(scheduleId);
        existingSchedule.setRoom("Room 100");
        existingSchedule.setDate(Date.valueOf(LocalDate.now()));

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));

        // Act
        ResponseEntity<?> response = scheduleController.updateEvent(scheduleId, scheduleDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Event updated successfully!", ((Map<?, ?>) response.getBody()).get("message"));
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }
}
