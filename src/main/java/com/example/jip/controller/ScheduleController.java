package com.example.jip.controller;

import com.example.jip.dto.ClassScheduleDTO;
import com.example.jip.dto.ScheduleAttendanceDTO;
import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import com.example.jip.services.EmailServices;
import com.example.jip.services.ScheduleServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ScheduleServices scheduleServices;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private EmailServices emailServices;

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
            return new ArrayList<>();
        }
        return schedules.stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/saveDraft")
    public ResponseEntity<?> saveDraft(@RequestBody List<ScheduleDTO> schedules) {
        try {
            if (schedules.isEmpty()) {
                return ResponseEntity.badRequest().body("No schedules provided.");
            }
            int classId = schedules.get(0).getClassId();
            Class clasz = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Class ID: " + classId));
            int semesterId = schedules.get(0).getSemesterId();
            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Semester ID: " + semesterId));

            for (ScheduleDTO scheduleDTO : schedules) {
                // Parse date from DTO
                LocalDate localDate = LocalDate.parse(scheduleDTO.getDate());
                java.sql.Date sqlDate = Date.valueOf(localDate);

                // Check for existing schedule
                Schedule schedule;
                if (scheduleDTO.getId() != null) {
                    schedule = scheduleRepository.findById(scheduleDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID: " + scheduleDTO.getId()));
                } else {
                    schedule = new Schedule();
                    schedule.setSemester(semester);
                }

                // Update or set fields
                schedule.setActivity(scheduleDTO.getActivity());
                schedule.setColor(scheduleDTO.getColor());
                schedule.setDate(sqlDate);
                schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                schedule.setSemester(semester);
                schedule.setClasz(clasz);
                schedule.setRoom(scheduleDTO.getRoom());
                schedule.setStatus(Schedule.status.Published);


                scheduleRepository.save(schedule);


            }

            return ResponseEntity.ok(Map.of(
                    "message", "Schedules saved successfully.",
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
            if (schedules.isEmpty()) {
                return ResponseEntity.badRequest().body("No schedules provided.");
            }

            int semesterId = schedules.get(0).getSemesterId();
            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Semester ID: " + semesterId));
            int classId = schedules.get(0).getClassId();
            Class clasz = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Class ID: " + classId));

            // Process each schedule
            for (ScheduleDTO scheduleDTO : schedules) {
                try {
                    LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                    java.sql.Date sqlDate = Date.valueOf(localDate);

                    Schedule draftSchedule = null;

                    // Check if the draft exists and retrieve it
                    if (scheduleDTO.getId() != null) {
                        draftSchedule = scheduleRepository.findById(scheduleDTO.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID: " + scheduleDTO.getId()));
                    }

                    // Clone the draft schedule or create a new schedule for publishing
                    Schedule publishedSchedule = new Schedule();
                    publishedSchedule.setSemester(semester);
                    publishedSchedule.setClasz(clasz);
                    publishedSchedule.setActivity(scheduleDTO.getActivity());
                    publishedSchedule.setColor(scheduleDTO.getColor());
                    publishedSchedule.setDate(sqlDate);
                    publishedSchedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                    publishedSchedule.setRoom(scheduleDTO.getRoom());
                    publishedSchedule.setStatus(Schedule.status.Published);

                    // Save the new published schedule
                    scheduleRepository.save(publishedSchedule);

                    // Optionally handle room availability and other related logic
                    if (scheduleDTO.getRoom() != null) {
                        Room availableRoom = roomRepository
                                .findFirstAvailableRoomByStatusAndDate(String.valueOf(RoomAvailability.Status.Available), sqlDate)
                                .orElseThrow(() -> new IllegalArgumentException("No available rooms for date: " + scheduleDTO.getDate()));
                        publishedSchedule.setRoom(availableRoom.getName());
                        roomAvailabilityRepository.findByRoomAndDate(availableRoom, sqlDate).ifPresent(roomAvailability -> {
                            roomAvailability.setStatus(RoomAvailability.Status.Occupied);
                            roomAvailability.setSchedule(publishedSchedule);
                            roomAvailability.setClasz(clasz);
                            roomAvailabilityRepository.save(roomAvailability);
                        });
                        createAttendantsForSchedule(publishedSchedule);
                    }

                } catch (Exception ex) {
                    // Log and continue
                    System.out.println("Error processing schedule: " + scheduleDTO + ". Error: " + ex.getMessage());
                    continue;
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Schedules saved successfully.",
                    "semesterId", semesterId
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Error saving schedules.",
                    "error", e.getMessage()
            ));
        }
    }


    private void createAttendantsForSchedule(Schedule schedule) {
        // Fetch students in the class
        Class clasz = schedule.getClasz();
        List<Student> students = clasz.getClassLists().stream()
                .map(Listt::getStudent)
                .collect(Collectors.toList());

        // Iterate and create an attendant for each student
        for (Student student : students) {
            Attendant attendant = new Attendant();
            attendant.setStudent(student);
            attendant.setSchedule(schedule);
            attendant.setStatus(null);
            attendant.setDate(schedule.getDate());
            attendant.setStartTime(Time.valueOf("13:30:00"));
            attendant.setEndTime(Time.valueOf("17:00:00"));
            attendant.setIsFinalized(false);

            attendantRepository.save(attendant);
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
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID: " + id));

            if (schedule.getRoom() != null) {
                roomRepository.findByName(schedule.getRoom()).ifPresent(room -> {
                    java.sql.Date sqlDate = schedule.getDate();
                    roomAvailabilityRepository.findByRoomAndDate(room, sqlDate).ifPresent(roomAvailability -> {
                        roomAvailability.setSchedule(null);
                        roomAvailability.setClasz(null);
                        roomAvailability.setStatus(RoomAvailability.Status.Available);
                        roomAvailabilityRepository.save(roomAvailability);
                    });
                });
            }
            scheduleRepository.deleteById(id);

            return ResponseEntity.ok("Event deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete event.");
        }
    }

    @GetMapping("/get/class/{classId}/student/{studentId}")
    public List<ScheduleAttendanceDTO> getScheduleWithAttendance(@PathVariable int classId, @PathVariable int studentId) {
        List<Object[]> results = scheduleRepository.findSchedulesWithAttendanceByClassIdNative(classId, studentId);
        return results.stream().map(record -> new ScheduleAttendanceDTO(
                ((Number) record[0]).longValue(),                      // scheduleId
                (Date) record[1],                     // scheduleDate
                Schedule.dayOfWeek.valueOf((String) record[2]), // dayOfWeek
                (String) record[3],                   // room
                (String) record[4],                   // activity
                ((Number) record[5]).intValue(),                      // studentId
                record[6] != null ? Attendant.Status.valueOf((String) record[6]) : null, // attendanceStatus
                (String) record[7]
        )).collect(Collectors.toList());
    }

    @GetMapping("/get/class/{classId}")
    public List<ScheduleDTO> getClassSchedule(@PathVariable int classId) {
        return scheduleRepository.findByClaszId(classId).stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getRoomForClass/{classId}")
    public List<ScheduleDTO> getRoomClassSchedule(@PathVariable int classId) {
        return scheduleRepository.findByClaszId(classId).stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable int id, @RequestBody ScheduleDTO scheduleDTO) {
        try {
            // Fetch the existing schedule
            Schedule existingSchedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));

            java.sql.Date currentDate = existingSchedule.getDate();

            // Release the previously assigned room if it exists
            if (existingSchedule.getRoom() != null) {
                roomRepository.findByName(existingSchedule.getRoom()).ifPresent(previousRoom -> {
                    roomAvailabilityRepository.findByRoomAndDate(previousRoom, currentDate).ifPresent(roomAvailability -> {
                        roomAvailability.setSchedule(null);
                        roomAvailability.setClasz(null);
                        roomAvailability.setStatus(RoomAvailability.Status.Available); // Mark as available
                        roomAvailabilityRepository.save(roomAvailability);
                    });
                });
            }

            // Update the new room's availability if a new room is provided
            if (scheduleDTO.getRoom() != null) {
                roomRepository.findByName(scheduleDTO.getRoom()).ifPresent(newRoom -> {
                    roomAvailabilityRepository.findByRoomAndDate(newRoom, currentDate).ifPresentOrElse(roomAvailability -> {
                        roomAvailability.setSchedule(existingSchedule);
                        roomAvailability.setClasz(existingSchedule.getClasz());
                        roomAvailability.setStatus(RoomAvailability.Status.Occupied); // Mark as occupied
                        roomAvailabilityRepository.save(roomAvailability);
                    }, () -> {
                        // If no room availability record exists, create a new one
                        RoomAvailability newRoomAvailability = new RoomAvailability();
                        newRoomAvailability.setRoom(newRoom);
                        newRoomAvailability.setDate(currentDate);
                        newRoomAvailability.setSchedule(existingSchedule);
                        newRoomAvailability.setClasz(existingSchedule.getClasz());
                        newRoomAvailability.setStatus(RoomAvailability.Status.Occupied);
                        roomAvailabilityRepository.save(newRoomAvailability);
                    });
                });
            }

            // Update the schedule's details
            existingSchedule.setActivity(scheduleDTO.getActivity());
            existingSchedule.setRoom(scheduleDTO.getRoom());
            existingSchedule.setColor(scheduleDTO.getColor());
            scheduleRepository.save(existingSchedule);

            Class clas = existingSchedule.getClasz();
            if (clas != null) {
                String teacherEmail = clas.getTeacher().getEmail();

                Set<String> studentEmails = clas.getClassLists().stream()
                        .map(list -> list.getStudent().getEmail())
                        .collect(Collectors.toSet());

                if (teacherEmail != null) {
                    emailServices.sendScheduleUpdate(teacherEmail, existingSchedule.getDate());
                }
                for (String email : studentEmails) {
                    emailServices.sendScheduleUpdate(email, existingSchedule.getDate());
                }
            }

            return ResponseEntity.ok(Map.of("message", "Event updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating event", "error", e.getMessage()));
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
