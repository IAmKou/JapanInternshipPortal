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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
//
//    @Autowired
//    ScheduleServices scheduleServices;
//
//    @Autowired
//    ScheduleRepository scheduleRepository;
//
//    @PostMapping("/import")
//    public ResponseEntity<?> importSchedules(@RequestParam("file") MultipartFile file) {
//        if (!file.getOriginalFilename().endsWith(".xlsx")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of("Invalid file format. Please upload an Excel file."));
//        }
//        try (InputStream inputStream = file.getInputStream()) {
//            List<String> errors = scheduleServices.importSchedules(inputStream);
//            if (errors.isEmpty()) {
//                return ResponseEntity.ok("Schedules imported successfully.");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error importing schedules: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/get")
//    public List<ScheduleDTO> getSchedules() {
//        return scheduleRepository.findAll().stream()
//                .map(ScheduleDTO::new)
//                .collect(Collectors.toList());
//    }
}
