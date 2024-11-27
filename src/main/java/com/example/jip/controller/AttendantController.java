package com.example.jip.controller;

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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendant")
public class AttendantController {

    @Autowired
    AttendantServices attendantServices;

    @Autowired
    ListRepository listRepository;

    @Autowired
    AttendantRepository attendantRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam int student_id,
                                       @RequestParam String status,
                                       @RequestParam Date date,
                                       @RequestParam String note,
                                       @RequestParam int class_id) {

        List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(class_id, date);

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance cannot be saved as the time does not match any schedule slot.");
        }


        int sid = matchingSchedule.getId();


        attendantServices.createAttendant(student_id, sid, status, date, note, class_id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Attendance saved successfully for the matching schedule slot.");
    }


    @GetMapping("/attendance")
    public List<Attendant> getAttendance(@PathVariable int scheduleId, @RequestParam Date date) {
        LocalTime currentTime = LocalTime.now();
        return attendantRepository.findByScheduleIdAndTimeSlot(scheduleId, date, Time.valueOf(currentTime));
    }
}
