package com.example.jip.controller;

import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.AttendantServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/attendant")
public class AttendantController {

    @Autowired
    private AttendantServices attendantServices;

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostMapping("/save/{classId}")
    public ResponseEntity<String> save(@RequestBody List<AttendantDTO> attendanceRequests, @PathVariable int classId) {
        if (attendanceRequests == null || attendanceRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance request list cannot be null or empty.");
        }

        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(13, 30))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance cannot be taken before 13:30.");
        }

        for (AttendantDTO request : attendanceRequests) {
            int studentId = request.getStudentId();
            Attendant.Status status = request.getStatus();
            Date date = request.getDate();

            // Validate date
            if (date == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date is missing in the request.");
            }

            // Fetch the schedule for the given class and date
            List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(classId, date);
            if (schedules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No schedule found for the given class and date.");
            }

            // Ensure only one schedule is used (for example, the first match)
            Schedule matchingSchedule = schedules.get(0);

            // Check if attendance is finalized
            boolean isFinalized = attendantRepository.existsByScheduleIdAndIsFinalizedTrue(matchingSchedule.getId());
            if (isFinalized) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance is finalized and cannot be updated.");
            }

            // Save attendance
            try {
                attendantServices.createAttendant(studentId, matchingSchedule.getId(), status.toString(), date, classId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Attendance saved successfully.");
    }





    @GetMapping("/{classId}/get")
    public List<AttendantDTO> getAttendance(@PathVariable int classId) {
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);
        return attendantRepository.findByDateAndClassId(date,classId);
    }

    @GetMapping("/{classId}/get/{date}")
    public List<AttendantDTO> getAttendanceManager(@PathVariable int classId, @PathVariable Date date) {
        return attendantRepository.findByDateAndClassId(date,classId);
    }

    @PostMapping("/update/{classId}")
    public ResponseEntity<Map<String, Object>> updateAttendance(
            @PathVariable("classId") int classId,
            @RequestBody List<AttendantDTO> attendanceData
    ) {
        Map<String, Object> response = new HashMap<>();
        List<String> logs = new ArrayList<>();

        try {
            logs.add("Starting attendance update for classId: " + classId);
            logs.add("Received attendance data: " + attendanceData);

            // Validate and update attendance records
            attendantServices.updateAttendance(classId, attendanceData);
            logs.add("Attendance updated successfully!");

            response.put("status", "success");
            response.put("logs", logs);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logs.add("Caught IllegalArgumentException: " + e.getMessage());
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (IllegalStateException e) {
            logs.add("Caught IllegalStateException: " + e.getMessage());
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            logs.add("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update/{classId}/date/{date}")
    public ResponseEntity<Map<String, Object>> updateAttendanceManager(
            @PathVariable("classId") int classId,
            @PathVariable("date") Date date,
            @RequestBody List<AttendantDTO> attendanceData
    ) {
        Map<String, Object> response = new HashMap<>();
        List<String> logs = new ArrayList<>();

        try {
            logs.add("Starting attendance update for classId: " + classId);
            logs.add("Received attendance data: " + attendanceData);

            // Validate and update attendance records
            attendantServices.updateAttendanceManager(classId,date, attendanceData);
            logs.add("Attendance updated successfully!");

            response.put("status", "success");
            response.put("logs", logs);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logs.add("Caught IllegalArgumentException: " + e.getMessage());
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (IllegalStateException e) {
            logs.add("Caught IllegalStateException: " + e.getMessage());
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            logs.add("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("logs", logs);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{classId}/attendanceStatus")
    public boolean hasAttendanceBeenTaken(@PathVariable Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("classId must not be null");
        }
        // Assuming you have a repository to check attendance
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);
        return attendantRepository.existsByClassIdAndDate(classId, date);
    }

    @GetMapping("/getAll/{classId}")
    public List<AttendantDTO> getAllAttendance(@PathVariable int classId) {
        return attendantRepository.findByClassId(classId);
    }



}
