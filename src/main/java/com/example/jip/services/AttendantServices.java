package com.example.jip.services;

import com.example.jip.entity.Attendant;
import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Service
public class AttendantServices {

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Attendant createAttendant(int student_id, int schedule_id, String status, Date date, int class_id) {
        List<Schedule> schedules = scheduleRepository.findByClassIdAndDate(class_id, date);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No schedule found for class_id: " + class_id + " and date: " + date);
        }

        // Find the schedule that matches the time range
        LocalTime currentLocalTime = LocalTime.now();
        Time currentTime = Time.valueOf(currentLocalTime);

        Schedule matchingSchedule = schedules.stream()
                .filter(schedule -> !currentTime.before(schedule.getStart_time()) && !currentTime.after(schedule.getEnd_time()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching schedule slot found for the current time"));

        // Fetch the student
        Optional<Student> studentOpt = studentRepository.findById(student_id);
        if (!studentOpt.isPresent()) {
            throw new IllegalArgumentException("No student found with id: " + student_id);
        }


        // Create and save the attendance record
        Attendant attendant = new Attendant();
        attendant.setStudent(studentOpt.get());
        attendant.setSchedule(matchingSchedule);
        attendant.setStatus(Attendant.Status.valueOf(status));
        attendant.setDate(date);

        return attendantRepository.save(attendant);
    }

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
        Schedule schedule = schedules.get(0);

        for (AttendantDTO dto : attendanceData) {
            // Fetch existing attendance record or create a new one
            Attendant attendant = attendantRepository
                    .findByStudentIdAndScheduleIdAndDate(dto.getStudentId(), schedule.getId(), dto.getDate())
                    .orElseGet(Attendant::new);

            // Fetch the student
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));

            // Update the attendance record
            attendant.setStudent(student);
            attendant.setSchedule(schedule);
            attendant.setStatus(dto.getStatus());
            attendant.setDate(dto.getDate());

            // Save the updated attendance
            attendantRepository.save(attendant);
        }
    }

}


