package com.example.jip.controller;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.dto.StudentScheduleDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.ScheduleServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScheduleControllerTest {

    private ScheduleController scheduleController;

    @Mock
    private ScheduleServices scheduleServices;

    @Mock
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleController = new ScheduleController();
        scheduleController.scheduleServices = scheduleServices;
        scheduleController.scheduleRepository = scheduleRepository;
    }

    @Test
    void testImportSchedules_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{});

        when(scheduleServices.importSchedules(mockFile)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = scheduleController.importSchedules(mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void testImportSchedules_InvalidFileFormat() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", new byte[]{});

        ResponseEntity<?> response = scheduleController.importSchedules(mockFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((Map<?, ?>) response.getBody()).get("status"));
    }

    @Test
    void testDeleteSchedule_Exists() {
        when(scheduleRepository.existsById(1)).thenReturn(true);

        boolean result = scheduleController.deleteSchedule(1);

        verify(scheduleRepository, times(1)).deleteById(1);
        assertEquals(true, result);
    }

    @Test
    void testDeleteSchedule_NotExists() {
        when(scheduleRepository.existsById(1)).thenReturn(false);

        boolean result = scheduleController.deleteSchedule(1);

        verify(scheduleRepository, never()).deleteById(1);
        assertEquals(false, result);
    }

    @Test
    void testUpdateSchedule_Success() {
        ScheduleDTO mockDTO = new ScheduleDTO();
        mockDTO.setStartTime(Time.valueOf("09:00:00"));
        mockDTO.setEndTime(Time.valueOf("10:00:00"));

        when(scheduleServices.updateSchedule(eq(1), any(), eq(mockDTO))).thenReturn(mockDTO);

        ResponseEntity<ScheduleDTO> response = scheduleController.updateSchedule(1, mockDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDTO, response.getBody());
    }

    @Test
    void testUpdateSchedule_NotFound() {
        when(scheduleServices.updateSchedule(eq(1), any(), any())).thenThrow(new NoSuchElementException());

        ResponseEntity<ScheduleDTO> response = scheduleController.updateSchedule(1, new ScheduleDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetStudentSchedule() {
        Object[] row = {1, "Math", "John Doe", "Room 101", "Monday", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"),
                "Present", Date.valueOf(LocalDate.now()), "Homework", "Holiday"};

        when(scheduleRepository.findStudentSchedule(1)).thenReturn(Collections.singletonList(row));

        List<StudentScheduleDTO> result = scheduleController.getStudentSchedule(1);

        assertEquals(1, result.size());
        assertEquals("Math", result.get(0).getClassName());
    }

    @Test
    void testSearchSchedules_ByClassName() {
        ScheduleDTO mockSchedule = new ScheduleDTO();
        mockSchedule.setId(1);

        when(scheduleRepository.findByClassName("Math")).thenReturn(Collections.singletonList(mockSchedule));

        List<ScheduleDTO> result = scheduleController.searchSchedules("Math", null, null);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
}
