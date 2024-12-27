package com.example.jip.controller;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import com.example.jip.repository.CurriculumRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    ScheduleRepository scheduleRepository;


    @DeleteMapping("/delete/{id}")
    public boolean deleteSchedule(@PathVariable int id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }
    @GetMapping("/get/{semesterId}")
    public List<ScheduleDTO> getSchedule(@PathVariable int semesterId) {
        List<Schedule> schedules = scheduleRepository.findBySemesterId(semesterId);
        if (schedules == null) {
            return new ArrayList<>(); // Return an empty list if no data
        }
        return schedules.stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/saveDraft")
    public ResponseEntity<?> saveDraft(@RequestBody List<ScheduleDTO> schedules) {
        try {
            // Validate semester ID from the first schedule
            if (schedules.isEmpty()) {
                return ResponseEntity.badRequest().body("No schedules provided.");
            }

            int semesterId = schedules.get(0).getSemesterId();
            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Semester ID: " + semesterId));

            // Delete existing schedules for the semester
            scheduleRepository.deleteBySemesterId(semesterId);

            // Save new schedules
            for (ScheduleDTO scheduleDTO : schedules) {
                Schedule schedule = new Schedule();
                schedule.setActivity(scheduleDTO.getActivity());
                schedule.setColor(scheduleDTO.getColor());
                LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                schedule.setDate(Date.valueOf(localDate));
                schedule.setDay_of_week(getDayOfWeekFromDate(Date.valueOf(localDate)));
                schedule.setSemester(semester);
                schedule.setTime_slot("All day");
                schedule.setStatus(Schedule.status.Draft);

                scheduleRepository.save(schedule);
            }
            return ResponseEntity.ok("Schedules updated successfully for semester ID: " + semesterId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating schedules");
        }
    }
    private Schedule.dayOfWeek getDayOfWeekFromDate(java.sql.Date date) {
        java.time.LocalDate localDate = date.toLocalDate();
        java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY:
                return Schedule.dayOfWeek.Monday;
            case TUESDAY:
                return Schedule.dayOfWeek.Tuesday;
            case WEDNESDAY:
                return Schedule.dayOfWeek.Wednesday;
            case THURSDAY:
                return Schedule.dayOfWeek.Thursday;
            case FRIDAY:
                return Schedule.dayOfWeek.Friday;
            case SATURDAY:
                return Schedule.dayOfWeek.Saturday;
            case SUNDAY:
                return Schedule.dayOfWeek.Sunday;
            default:
                return null; // In case of an invalid date, you can return null or handle it differently
        }
    }
}
