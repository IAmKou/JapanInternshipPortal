package com.example.jip.controller;

import com.example.jip.dto.SemesterDTO;
import com.example.jip.entity.Holiday;
import com.example.jip.entity.Semester;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.SemesterRepository;
import com.example.jip.services.HolidayServices;
import com.example.jip.services.SemesterServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/semester")
public class SemesterController {
    @Autowired
    private SemesterServices semesterService;

    @Autowired
    private HolidayServices holidayService;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostMapping("/create")
    public ResponseEntity<String> createSemester(@RequestBody Semester semester) {
        try {
            if (semester.getStart_time() == null || semester.getEnd_time() == null) {
                throw new IllegalArgumentException("Start time and end time must not be null");
            }

            if (semesterService.isSemesterNameExist(semester.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"message\":\"Semester name already exists\"}");
            }

            if (semesterService.isStartTimeWithinExistingSemester(semester.getStart_time())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"message\":\"Start time overlaps with an existing semester\"}");
            }

            // Save the semester
            semesterService.saveSemester(semester);

            // Fetch holidays and other processing
            String startDate = semester.getStart_time().toString();
            String endDate = semester.getEnd_time().toString();
            String year = String.valueOf(semester.getStart_time().toLocalDate().getYear());

            List<Holiday> holidays = holidayService.fetchHolidays("JP", year, startDate, endDate);

            // Add holidays to semester and schedule
            semesterService.addHolidaysToSemester(holidays);
            semesterService.addHolidaysToSchedule(semester, holidays);

            // Return a success message with a proper JSON response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"message\":\"Semester created successfully with holidays in the schedule!\"}");
        } catch (Exception e) {
            log.error("Error occurred while creating semester: ", e);

            // Return an error response with a proper JSON format
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Error occurred while creating semester: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/get")
    public List<SemesterDTO> getSemester() {
        return semesterRepository.findAll().stream()
                .map(SemesterDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/get/{semesterId}")
    public SemesterDTO getSemesterById(@PathVariable int semesterId) {
        return semesterRepository.findById(semesterId)
                .map(SemesterDTO::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semester not found"));
    }
}