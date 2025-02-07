package com.example.jip.services;

import com.example.jip.entity.Attendant;
import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AttendantServices {

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Attendant createAttendant(int studentId, int scheduleId, String status, Date date, int classId) {
        // Fetch the schedule
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            throw new IllegalArgumentException("No schedule found with id: " + scheduleId);
        }

        Schedule matchingSchedule = scheduleOpt.get();

        // Validate that the schedule belongs to the correct class
        if (matchingSchedule.getClasz() == null || matchingSchedule.getClasz().getId() != classId) {
            throw new IllegalArgumentException("Schedule does not belong to the specified class.");
        }


        // Fetch the student
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            throw new IllegalArgumentException("No student found with id: " + studentId);
        }

        // Check if attendance already exists for the student, schedule, and date
        List<Attendant> existingAttendants = attendantRepository.findByStudentIdAndScheduleIdAndDate(studentId, scheduleId, date);
        if (!existingAttendants.isEmpty()) {
            throw new IllegalArgumentException("Attendance already exists for this student, schedule, and date.");
        }

        // Check time constraints
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(13, 30)) || now.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Attendance can only be taken between 13:30 and 17:00.");
        }

        // Create and save the attendance record
        Attendant attendant = new Attendant();
        attendant.setStudent(studentOpt.get());
        attendant.setSchedule(matchingSchedule);
        attendant.setStatus(Attendant.Status.valueOf(status.toUpperCase()));
        attendant.setDate(date);

        return attendantRepository.save(attendant);
    }




    @Transactional
    public void updateAttendance(int classId, List<AttendantDTO> attendanceData) {
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        // Fetch schedules for the class and date
        List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(classId, date);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No schedule found for class ID " + classId + " and date " + date);
        }
        if (schedules.size() > 1) {
            throw new IllegalStateException("Multiple schedules found for class ID " + classId + " and date " + date);
        }
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(6, 0)) || now.isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("Attendance can only be taken between 6:00 AM and 12:00 AM.");
        }
        Schedule schedule = schedules.get(0);

        List<Attendant> attendantsToUpdate = new ArrayList<>();

        for (AttendantDTO dto : attendanceData) {
            // Validate DTO
            if (dto.getDate() == null || !dto.getDate().toLocalDate().equals(today)) {
                throw new IllegalArgumentException("Invalid or mismatched date in attendance record.");
            }
            if (!dto.isFinalized()) {
                throw new IllegalArgumentException("Attendance record is finalized.");
            }

            // Fetch existing attendance record or create a new one
            Attendant attendant = attendantRepository
                    .findByStudentIdAndScheduleIdAndDates(dto.getStudentId(), schedule.getId(), dto.getDate())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Attendance record not found for student ID " + dto.getStudentId() + ", schedule ID " + schedule.getId() + " and date " + dto.getDate()));

            // Fetch the student
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));

            // Update the attendance record
            attendant.setStudent(student);
            attendant.setSchedule(schedule);
            attendant.setStatus(dto.getStatus());
            attendant.setDate(dto.getDate());
            attendant.setStartTime(Time.valueOf("13:30:00"));
            attendant.setEndTime(Time.valueOf("17:00:00"));
            attendant.setIsFinalized(dto.isFinalized());

            attendantsToUpdate.add(attendant);
        }

        // Save updated attendance records
        attendantRepository.saveAll(attendantsToUpdate);
    }

    @Transactional
    public void updateAttendanceManager(int classId, Date date,List<AttendantDTO> attendanceData) {

        List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(classId, date);

        List<Attendant> attendantsToUpdate = new ArrayList<>();
        Schedule schedule = schedules.get(0);

        for (AttendantDTO dto : attendanceData) {

            Attendant attendant = attendantRepository
                    .findByStudentIdAndScheduleIdAndDates(dto.getStudentId(), schedule.getId(), dto.getDate())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Attendance record not found for student ID " + dto.getStudentId() + ", schedule ID " + schedule.getId() + " and date " + dto.getDate()));

            // Fetch the student
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));

            // Update the attendance record
            attendant.setStudent(student);
            attendant.setSchedule(schedule);
            attendant.setStatus(dto.getStatus());
            attendant.setDate(dto.getDate());
            attendant.setStartTime(Time.valueOf("13:30:00"));
            attendant.setEndTime(Time.valueOf("17:00:00"));
            attendant.setIsFinalized(dto.isFinalized());

            attendantsToUpdate.add(attendant);
        }

        // Save updated attendance records
        attendantRepository.saveAll(attendantsToUpdate);
    }


}


