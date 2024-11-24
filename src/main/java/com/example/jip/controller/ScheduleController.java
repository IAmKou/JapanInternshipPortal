package com.example.jip.controller;

import com.example.jip.services.ScheduleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleServices scheduleServices;

    @PostMapping("/import")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of("Invalid file format. Please upload an Excel file."));
        }
        try{
            scheduleServices.importSchedules(file.getInputStream());
            return ResponseEntity.ok("Schedules imported successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error importing schedules: " + e.getMessage());
        }
    }
}
