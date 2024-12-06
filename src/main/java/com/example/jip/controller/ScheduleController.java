package com.example.jip.controller;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.dto.StudentScheduleDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.services.ScheduleServices;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleServices scheduleServices;

    @Autowired
    ScheduleRepository scheduleRepository;

    @PostMapping("/import")
    public ResponseEntity<?> importSchedules(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", false, "message", "Invalid file format. Please upload an Excel file."));
        }
        try {
            List<String> errors = scheduleServices.importSchedules(file);
            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of("status", false, "data", errors));
            } else {
                return ResponseEntity.ok(Map.of("status", true, "message", "Successfully imported schedules."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", false, "message", "Error importing schedules: " + e.getMessage()));
        }
    }


    @GetMapping("/get")
    public List<ScheduleDTO> getSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteSchedule(@PathVariable int id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable int id, @RequestBody ScheduleDTO dto) {


        if (dto.getStartTime() != null && dto.getStartTime().toString().length() == 5) {
            String adjustedStartTime = dto.getStartTime().toString() + ":00";
            dto.setStartTime(Time.valueOf(adjustedStartTime));
        }

        if (dto.getEndTime() != null && dto.getEndTime().toString().length() == 5) {
            String adjustedEndTime = dto.getEndTime().toString() + ":00";
            dto.setEndTime(Time.valueOf(adjustedEndTime));
        }

        try {
            ScheduleDTO updatedSchedule = scheduleServices.updateSchedule(id, dto.getClassName(), dto);
            return ResponseEntity.ok(updatedSchedule);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getS/{studentId}")
    public List<StudentScheduleDTO> getStudentSchedule(@PathVariable int studentId) {

        List<Object[]> result = scheduleRepository.findStudentSchedule(studentId);
        List<StudentScheduleDTO> scheduleDTOs = new ArrayList<>();
        for (Object[] row : result) {
            int scheduleId = (int) row[0];  // schedule_id
            String className = (String) row[1];  // class_name
            String teacherName = (String) row[2];
            String location = (String) row[3];// teacher_name
            Schedule.dayOfWeek dayOfWeek = Schedule.dayOfWeek.valueOf((String) row[4]);
            Time startTime = (Time) row[5];  // start_time
            Time endTime = (Time) row[6];  // end_time
            Attendant.Status attendanceStatus = Attendant.Status.valueOf((String) row[7]);  // attendance_status
            Date date = (Date) row[8];  // date
            String description = (String) row[9];  // description
            String event = (String) row[10];  // event

            // Create DTO and add to list
            StudentScheduleDTO dto = new StudentScheduleDTO(
                    scheduleId, className, teacherName,location, dayOfWeek, startTime, endTime,
                    attendanceStatus, date, description, event
            );

            scheduleDTOs.add(dto);
        }

        return scheduleDTOs;
    }

    @GetMapping("/getT/{teacherId}")
    public List<ScheduleDTO> getScheduleForTeacher(@PathVariable int teacherId) {
        List<ScheduleDTO> result = scheduleRepository.findTeacherSchedule(teacherId);
        return result;
    }

    @GetMapping("/search")
    public List<ScheduleDTO> searchSchedules(@RequestParam(name="className", required = false) String className,
                                             @RequestParam(name="teacherName", required = false) String teacherName,
                                             @RequestParam(name="date", required = false) String date) {
        System.out.println("Class Name: " + className);
        System.out.println("Teacher Name: " + teacherName);
        System.out.println("Date: " + date);

        LocalDate localDate = null;
        if (date != null && !date.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format: " + e.getMessage());
            }
        }

        // Convert to java.sql.Date if valid
        java.sql.Date sqlDate = null;
        if (localDate != null) {
            sqlDate = java.sql.Date.valueOf(localDate);
        }

        // Debugging the state of sqlDate
        System.out.println("Parsed sqlDate: " + sqlDate);

        // Apply the conditions based on the available parameters
        if (className != null && teacherName != null && sqlDate != null) {
            System.out.println("Running the first case");
            return scheduleRepository.findByClassNameAndTeacherNameAndDate(className, teacherName, sqlDate);
        } else if (className != null) {
            System.out.println("Running the fifth case");
            return scheduleRepository.findByClassName(className);
        } else if (teacherName != null) {
            System.out.println("Running the sixth case");
            return scheduleRepository.findByTeacherName(teacherName);
        } else if (sqlDate != null) {
            System.out.println("Running the seventh case");
            return scheduleRepository.findByDate(sqlDate).stream()
                    .map(ScheduleDTO::new)
                    .collect(Collectors.toList());
        } else {
            System.out.println("Running the eighth case (default case)");
            return scheduleRepository.findAll().stream()
                    .map(ScheduleDTO::new)
                    .collect(Collectors.toList());
        }
    }

}
