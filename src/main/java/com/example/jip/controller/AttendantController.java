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

//    @Autowired
//    AttendantServices attendantServices;
//
//    @Autowired
//    ListRepository listRepository;
//
//    @Autowired
//    AttendantRepository attendantRepository;
//
//    @Autowired
//    ScheduleRepository scheduleRepository;
//
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestParam int student_id,
//                                          @RequestParam int schedule_id,
//                                          @RequestParam String status,
//                                          @RequestParam Date date,
//                                          @RequestParam String note,
//                                          @RequestParam int class_id) {
//        Optional<Schedule> schedule = scheduleRepository.findByClassIdAndDate(class_id, date);
//        Time startTime = schedule.get().getStart_time();
//        Time endTime = schedule.get().getEnd_time();
//
//        LocalTime currentLocalTime = LocalTime.now();
//        Time currentTime = Time.valueOf(currentLocalTime);
//
//        if(currentTime.after(startTime) && currentTime.before(endTime)) {
//            attendantServices.createAttendant(student_id, schedule_id, status, date, note, class_id);
//            return ResponseEntity.status(HttpStatus.CREATED).body("Attendance saved successfully.");
//        }else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance cannot be saved as the time is outside the allowed range.");
//        }
//    }
//
//    @GetMapping("/attendance")
//    public List<Attendant> getAttendance(@PathVariable int scheduleId, @RequestParam Date date) {
//        LocalTime currentTime = LocalTime.now();
//        return attendantRepository.findByScheduleIdAndTimeSlot(scheduleId, date, Time.valueOf(currentTime));
//    }
}
