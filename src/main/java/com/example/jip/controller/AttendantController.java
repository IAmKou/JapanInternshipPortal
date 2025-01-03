package com.example.jip.controller;

import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.AttendantServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


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

    @PostMapping("/update/{classId}")
    public ResponseEntity<String> updateAttendance(
            @PathVariable("classId") int classId,
            @RequestBody List<AttendantDTO> attendanceData
    ) {
        try {
            // Validate and update attendance records
            attendantServices.updateAttendance(classId, attendanceData);
            return ResponseEntity.ok("Attendance updated successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update attendance: " + e.getMessage());
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


}
