package com.example.jip.controller;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.ScheduleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleServices scheduleServices;

    @Autowired
    ScheduleRepository scheduleRepository;

    @PostMapping("/import")
    public ResponseEntity<?> importSchedules(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", false, "message", "Invalid file format. Please upload an Excel file."));
        }
        try {
            List<String> errors = scheduleServices.importSchedules(file);
            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of("status", false, "data", errors));
            } else {
                return ResponseEntity.ok(Map.of("status", true, "message", "Successfully imported schedules."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", false, "message", "Error importing schedules: " + e.getMessage()));
        }
    }


    @GetMapping("/get")
    public List<ScheduleDTO> getSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteSchedule(@PathVariable int id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable int id, @RequestBody ScheduleDTO dto) {


        if (dto.getStartTime() != null && dto.getStartTime().toString().length() == 5) {
            String adjustedStartTime = dto.getStartTime().toString() + ":00";
            dto.setStartTime(Time.valueOf(adjustedStartTime));
        }

        if (dto.getEndTime() != null && dto.getEndTime().toString().length() == 5) {
            String adjustedEndTime = dto.getEndTime().toString() + ":00";
            dto.setEndTime(Time.valueOf(adjustedEndTime));
        }

        try {
            ScheduleDTO updatedSchedule = scheduleServices.updateSchedule(id, dto.getClassName(), dto);
            return ResponseEntity.ok(updatedSchedule);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getS/{id}")
    public List<ScheduleDTO> getScheduleForStudent(@PathVariable int id) {
        return scheduleRepository.findStudentSchedule(id).stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getT/{id}")
    public List<ScheduleDTO> getScheduleForTeacher(@PathVariable int id) {
        return scheduleRepository.findTeacherSchedule(id).stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }
}
