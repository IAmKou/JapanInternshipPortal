package com.example.jip.controller;

import com.example.jip.dto.AttendantDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.AttendantServices;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/attendant")
public class AttendantController {

    @Autowired
    private AttendantServices attendantServices;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostMapping("/save/{classId}")
    public ResponseEntity<String> save(@RequestBody List<AttendantDTO> attendanceRequests, @PathVariable int classId) {
        System.out.println("Received attendance requests: " + attendanceRequests);

        for (AttendantDTO request : attendanceRequests) {
            int studentId = request.getStudentId();
            Attendant.Status status = request.getStatus();
            Date date = request.getDate();

            if (date == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date is missing in the request.");
            }

            List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(classId, date);
            if (schedules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No schedule found for the given class and date.");
            }

            LocalTime currentLocalTime = LocalTime.now();
            Time currentTime = Time.valueOf(currentLocalTime);

            Schedule matchingSchedule = schedules.stream()
                    .filter(schedule -> !currentTime.before(schedule.getStart_time()) && !currentTime.after(schedule.getEnd_time()))
                    .findFirst()
                    .orElse(null);

            if (matchingSchedule == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Attendance cannot be saved as the time does not match any schedule slot.");
            }

            int scheduleId = matchingSchedule.getId();
            // Create attendant record only if all conditions are valid
            attendantServices.createAttendant(studentId, scheduleId, String.valueOf(status), date, classId);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Attendance saved successfully.");
    }



    @GetMapping("/attendance")
    public List<Attendant> getAttendance(@PathVariable int scheduleId, @RequestParam Date date) {
        LocalTime currentTime = LocalTime.now();
        return attendantRepository.findByScheduleIdAndTimeSlot(scheduleId, date, Time.valueOf(currentTime));
    }
}
