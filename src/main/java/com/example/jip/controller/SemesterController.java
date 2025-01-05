package com.example.jip.controller;

import com.example.jip.dto.SemesterDTO;
import com.example.jip.dto.SemesterDateDTO;
import com.example.jip.entity.Holiday;
import com.example.jip.entity.Semester;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.SemesterRepository;
import com.example.jip.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.Map;
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
    private ClassServices classServices;

    @Autowired
    private RoomAvailabilityServices roomAvailabilityServices;

    @Autowired
    private ExamService examService;
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
            int id = semester.getId();
            String startDate = semester.getStart_time().toString();
            String endDate = semester.getEnd_time().toString();
            String year = String.valueOf(semester.getStart_time().toLocalDate().getYear());

            List<Holiday> holidays = holidayService.fetchHolidays("JP", year, startDate, endDate);

            // Add holidays to semester and schedule
            semesterService.addHolidaysToSemester(holidays);
            semesterService.addHolidaysToSchedule(semester, holidays);
            roomAvailabilityServices.initializeRoomAvailabilityForSemester(id);
            examService.createExams(44, semester.getName());
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

    @GetMapping("/class/{classId}")
    public ResponseEntity<SemesterDateDTO> getSemesterDates(@PathVariable int classId) {
        SemesterDateDTO semesterDates = classServices.getSemesterDatesByClassId(classId);
        if (semesterDates == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(semesterDates);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSemester(@RequestBody Map<String, Integer> payload) {
        Integer sid = payload.get("sid");

        if (sid == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Semester ID is missing");
        }

        Semester semester = semesterRepository.findById(sid).orElse(null);

        if (semester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Semester not found");
        }

        if (!semester.getSchedules().isEmpty() || !semester.getClasses().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot delete semester. It is associated with schedules or classes.");
        }

        semesterRepository.delete(semester);
        return ResponseEntity.ok("Semester deleted successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateSemester(@RequestBody Semester updatedSemester) {
        Semester existingSemester = semesterRepository.findById(updatedSemester.getId()).orElse(null);

        if (existingSemester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Semester not found");
        }

        if (existingSemester.getStart_time().before(new java.util.Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot update semester. It has already started.");
        }
        if (semesterService.isStartTimeWithinExistingSemester(existingSemester.getStart_time())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"message\":\"Start time overlaps with an existing semester\"}");
        } else if (semesterService.isEndTimeWithinExistingSemester(existingSemester.getEnd_time())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"message\":\"End time overlaps with an existing semester\"}");
        }

        existingSemester.setName(updatedSemester.getName());
        existingSemester.setStart_time(updatedSemester.getStart_time());
        existingSemester.setEnd_time(updatedSemester.getEnd_time());

        semesterRepository.save(existingSemester);
        return ResponseEntity.ok("Semester updated successfully");
    }

}
