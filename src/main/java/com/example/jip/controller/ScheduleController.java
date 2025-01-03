package com.example.jip.controller;

import com.example.jip.dto.ClassScheduleDTO;
import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import com.example.jip.services.ScheduleServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
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
            // Validate schedules
            if (schedules.isEmpty()) {
                return ResponseEntity.badRequest().body("No schedules provided.");
            }

            // Validate semester and class
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

            // Process each schedule
            for (ScheduleDTO scheduleDTO : schedules) {
                try {
                    LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                    java.sql.Date sqlDate = Date.valueOf(localDate);

                    // Fetch and update draft schedules
                    if (scheduleDTO.getStatus() == Schedule.status.Draft) {
                        Room availableRoom = roomRepository
                                .findFirstAvailableRoomByStatusAndDate(String.valueOf(RoomAvailability.Status.Available), sqlDate)
                                .orElseThrow(() -> new IllegalArgumentException("No available rooms for date: " + scheduleDTO.getDate()));

                        Schedule draftSchedule = scheduleRepository.findById(scheduleDTO.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID: " + scheduleDTO.getId()));

                        // Assign new room
                        draftSchedule.setRoom(availableRoom.getName());
                        scheduleRepository.save(draftSchedule);

                        // Update room availability
                        roomAvailabilityRepository.findByRoomAndDate(availableRoom, sqlDate).ifPresent(roomAvailability -> {
                            roomAvailability.setStatus(RoomAvailability.Status.Occupied);
                            roomAvailability.setSchedule(draftSchedule);
                            roomAvailability.setClasz(clasz);
                            roomAvailabilityRepository.save(roomAvailability);
                        });
                    }
                    Schedule schedule;
                    if (scheduleDTO.getId() != null) {
                        schedule = scheduleRepository.findById(scheduleDTO.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID: " + scheduleDTO.getId()));
                    } else {
                        schedule = new Schedule();
                        schedule.setSemester(semester);
                    }

                    schedule.setActivity(scheduleDTO.getActivity());
                    schedule.setColor(scheduleDTO.getColor());
                    schedule.setDate(sqlDate);
                    schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                    schedule.setSemester(semester);
                    schedule.setClasz(clasz);
                    schedule.setRoom(scheduleDTO.getRoom());
                    schedule.setStatus(Schedule.status.Published);

                    scheduleRepository.save(schedule);
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




    @PostMapping("/update")
    public ResponseEntity<?> updateSchedule(@RequestBody List<ScheduleDTO> schedules) {
        try {
            if (schedules.isEmpty()) {
                log.warn("No schedules provided in the request.");
                return ResponseEntity.badRequest().body("No schedules provided.");
            }

            int semesterId = schedules.get(0).getSemesterId();
            int classId = schedules.get(0).getClassId();

            log.info("Received request to update schedules for Semester ID: {} and Class ID: {}", semesterId, classId);

            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> {
                        log.error("Invalid Semester ID: {}", semesterId);
                        return new IllegalArgumentException("Invalid Semester ID: " + semesterId);
                    });

            Class clasz = classRepository.findById(classId)
                    .orElseThrow(() -> {
                        log.error("Invalid Class ID: {}", classId);
                        return new IllegalArgumentException("Invalid Class ID: " + classId);
                    });

            for (ScheduleDTO scheduleDTO : schedules) {
                log.debug("Processing schedule: {}", scheduleDTO);

                LocalDate localDate = LocalDate.parse(scheduleDTO.getDate()).plusDays(1);
                java.sql.Date sqlDate = Date.valueOf(localDate);

                Schedule schedule;
                if (scheduleDTO.getId() != null) {
                    schedule = scheduleRepository.findById(scheduleDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("No schedule found with ID: " + scheduleDTO.getId()));
                } else {
                    log.info("Creating a new schedule as ID is null");
                    schedule = new Schedule();
                    schedule.setSemester(semester);
                    schedule.setClasz(clasz);
                    // Persist the new Schedule entity
                    schedule = scheduleRepository.save(schedule);
                }

                // Handle room assignment
                if (scheduleDTO.getRoom() != null) {
                    Schedule finalSchedule = schedule;
                   ;
                    roomRepository.findByName(scheduleDTO.getRoom()).ifPresent(newRoom -> {
                        roomAvailabilityRepository.findByRoomAndDate(newRoom, sqlDate)
                                .ifPresentOrElse(roomAvailability -> {
                                    roomAvailability.setSchedule(finalSchedule);
                                    roomAvailability.setClasz(clasz);
                                    roomAvailability.setStatus(RoomAvailability.Status.Occupied);
                                    roomAvailabilityRepository.save(roomAvailability);
                                }, () -> {
                                    RoomAvailability roomAvailability = new RoomAvailability();
                                    roomAvailability.setRoom(newRoom);
                                    roomAvailability.setDate(sqlDate);
                                    roomAvailability.setSchedule(finalSchedule); // Reference persisted schedule
                                    roomAvailability.setClasz(clasz);
                                    roomAvailability.setStatus(RoomAvailability.Status.Occupied);
                                    roomAvailabilityRepository.save(roomAvailability);
                                });
                    });
                }

                // Update the schedule details
                schedule.setActivity(scheduleDTO.getActivity());
                schedule.setColor(scheduleDTO.getColor());
                schedule.setDate(sqlDate);
                schedule.setDay_of_week(getDayOfWeekFromDate(sqlDate));
                schedule.setRoom(scheduleDTO.getRoom());
                schedule.setStatus(Schedule.status.Published);

                // Save the updated Schedule entity
                scheduleRepository.save(schedule);
                log.info("Schedule saved successfully for activity: {}", scheduleDTO.getActivity());
            }



            log.info("All schedules updated successfully for Semester ID: {} and Class ID: {}", semesterId, classId);
            return ResponseEntity.ok(Map.of(
                    "message", "Schedules updated successfully.",
                    "semesterId", semesterId
            ));
        } catch (Exception e) {
            log.error("An error occurred while updating schedules: {}", e.getMessage(), e);
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
