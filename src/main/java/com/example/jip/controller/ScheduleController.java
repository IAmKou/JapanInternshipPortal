package com.example.jip.controller;

import com.example.jip.dto.ClassScheduleDTO;
import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Class;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.CurriculumRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.SemesterRepository;
import com.example.jip.services.ScheduleServices;
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

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ScheduleServices scheduleServices;


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
        List<Schedule> schedules = scheduleRepository.findBySemesterIdAndStatus(semesterId, Schedule.status.valueOf("Draft"));
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

            for (ScheduleDTO scheduleDTO : schedules) {
                // Check if a schedule already exists for the semester, date, and activity
                LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                java.sql.Date sqlDate = Date.valueOf(localDate);

                Schedule existingSchedule = scheduleRepository.findBySemesterIdAndDateAndActivity(
                        semesterId, sqlDate, scheduleDTO.getActivity()
                );

                Schedule schedule;
                if (existingSchedule != null) {
                    // Update the existing schedule
                    schedule = existingSchedule;
                } else {
                    // Create a new schedule
                    schedule = new Schedule();
                    schedule.setSemester(semester);
                }

                schedule.setActivity(scheduleDTO.getActivity());
                schedule.setColor(scheduleDTO.getColor());
                schedule.setDate(sqlDate);
                schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                schedule.setTime_slot("All day");
                schedule.setStatus(Schedule.status.Draft);

                scheduleRepository.save(schedule);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Schedules saved successfully as draft.",
                    "semesterId", semesterId
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An error occurred while saving schedules.",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/savePublic")
    public ResponseEntity<?> savePublic(@RequestBody List<ScheduleDTO> schedules) {
            try {
                // Validate semester ID from the first schedule
                if (schedules.isEmpty()) {
                    return ResponseEntity.badRequest().body("No schedules provided.");
                }

                int semesterId = schedules.get(0).getSemesterId();
                Semester semester = semesterRepository.findById(semesterId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Semester ID: " + semesterId));
                int classId = schedules.get(0).getClassId();
                Class clasz = classRepository.findById(classId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Class ID: " + classId));
                boolean classHasSchedule = scheduleRepository.existsByClaszIdAndSemesterId(classId, semesterId);
                if (classHasSchedule) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "message", "The class already has a schedule for this semester."
                    ));
                }

                // Save new schedules
                for (ScheduleDTO scheduleDTO : schedules) {
                    LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                    java.sql.Date sqlDate = Date.valueOf(localDate);

                    boolean roomInUse = scheduleRepository.existsByRoomAndSemesterIdAndClassNot(
                            scheduleDTO.getRoom(), semesterId, classId);

                    if (roomInUse) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "message", "The room is already in use by another class."
                        ));
                    }
                    Schedule schedule = new Schedule();
                    schedule.setActivity(scheduleDTO.getActivity());
                    schedule.setColor(scheduleDTO.getColor());
                    schedule.setDate(sqlDate);
                    schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                    schedule.setSemester(semester);
                    schedule.setClasz(clasz);
                    schedule.setRoom(scheduleDTO.getRoom());
                    schedule.setTime_slot("All day");
                    schedule.setStatus(Schedule.status.Published);

                    scheduleRepository.save(schedule);
                }
                return ResponseEntity.ok(Map.of(
                        "message", "Schedules updated successfully",
                        "semesterId", semesterId
                ));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Error updating schedules",
                        "error", e.getMessage()
                ));
            }
        }

    @PostMapping("/update")
    public ResponseEntity<?> updateSchedule(@RequestBody List<ScheduleDTO> schedules) {
        try {
            // Validate semester ID from the first schedule
            if (schedules.isEmpty()) {
                return ResponseEntity.badRequest().body("No schedules provided.");
            }

            int semesterId = schedules.get(0).getSemesterId();
            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Semester ID: " + semesterId));

            int classId = schedules.get(0).getClassId();
            Class clasz = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Class ID: " + classId));

            for (ScheduleDTO scheduleDTO : schedules) {
                // Check if a schedule already exists for the semester, date, and activity
                LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                java.sql.Date sqlDate = Date.valueOf(localDate);

                Schedule existingSchedule = scheduleRepository.findBySemesterIdAndDateAndActivityAndClasz(
                        semesterId, sqlDate, scheduleDTO.getActivity(), classId
                );

                boolean roomInUse = scheduleRepository.existsByRoomAndSemesterIdAndClassNot(
                        scheduleDTO.getRoom(), semesterId, classId);

                if (roomInUse) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "message", "The room is already in use by another class."
                    ));
                }


                Schedule schedule;
                if (existingSchedule != null) {
                    // Update the existing schedule
                    schedule = existingSchedule;
                } else {
                    // Create a new schedule
                    schedule = new Schedule();
                    schedule.setSemester(semester);
                }

                schedule.setActivity(scheduleDTO.getActivity());
                schedule.setColor(scheduleDTO.getColor());
                schedule.setDate(sqlDate);
                schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                schedule.setClasz(clasz);
                schedule.setRoom(scheduleDTO.getRoom());
                schedule.setTime_slot("All day");
                schedule.setStatus(Schedule.status.Published);

                scheduleRepository.save(schedule);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Schedules saved successfully updated.",
                    "semesterId", semesterId
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An error occurred while updating schedules.",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/unique")
    public List<ClassScheduleDTO> getUniqueSchedule() {
        return scheduleServices.getUniqueClassSchedule();
    }

    @DeleteMapping("/schedule/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable int id) {
        try {
            if (!scheduleRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found.");
            }
            scheduleRepository.deleteById(id);
            return ResponseEntity.ok("Event deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete event.");
        }
    }

    @GetMapping("/get/class/{classId}")
    public List<ScheduleDTO> getClassSchedule(@PathVariable int classId) {
        return scheduleRepository.findByClaszId(classId).stream()
                .map(ScheduleDTO :: new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getRoomForClass/{classId}")
    public List<ScheduleDTO> getRoomClassSchedule(@PathVariable int classId) {
        return scheduleRepository.findByClaszId(classId).stream()
                .map(ScheduleDTO :: new)
                .collect(Collectors.toList());
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
