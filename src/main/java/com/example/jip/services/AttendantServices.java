package com.example.jip.services;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Class;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ScheduleRepository;
import com.example.jip.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class AttendantServices {

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    StudentRepository studentRepository;

    public Attendant createAttendant(int student_id, int schedule_id, String status, Date date, String note, int class_id ) {
        Optional<Schedule> scheduleOpt = scheduleRepository.findByClassIdAndDate(class_id, date);
        if (!scheduleOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + schedule_id);
        }
        Optional<Student> studentOpt = studentRepository.findById(student_id);
        if (!studentOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + student_id);
        }

        Attendant attendant = new Attendant();
        attendant.setStudent(studentOpt.get());
        attendant.setSchedule(scheduleOpt.get());
        attendant.setStatus(Attendant.Status.valueOf(status));
        attendant.setDate(date);
        attendant.setNote(note);

        return attendantRepository.save(attendant);
    }

    public void updateAttendance(int attendantId, Attendant.Status status, String note) {

        Attendant attendant = attendantRepository.findById(attendantId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));


        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();


        LocalDate attendanceDate = attendant.getDate().toLocalDate();


        if (!attendanceDate.isEqual(today) || now.isAfter(LocalTime.MIDNIGHT)) {
            throw new RuntimeException("Attendance can no longer be updated after midnight.");
        }

        attendant.setStatus(status);
        attendant.setNote(note);

        attendantRepository.save(attendant);
    }
}


