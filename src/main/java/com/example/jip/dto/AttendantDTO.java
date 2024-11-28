package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;

import java.sql.Date;

public class AttendantDTO {
    private int attendantId;
    private Attendant.Status status;
    public Date date;
    public String note;
    private int studentId;
    private int scheduleId;
    public Student student;
    public Schedule schedule;

    public AttendantDTO() {}

    public AttendantDTO(Attendant attendant) {
        this.attendantId = attendant.getId();
        this.studentId = attendant.getStudent().getId();
        this.scheduleId = attendant.getSchedule().getId();
        this.status = attendant.getStatus();
        this.date = attendant.getDate();
        this.note = attendant.getNote();
    }

    public AttendantDTO (int studentId, int scheduleId,Attendant.Status status, Date date) {
        this.studentId = studentId;
        this.scheduleId = scheduleId;
        this.status = status;
        this.date = date;
    }


    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(int attendantId) {
        this.attendantId = attendantId;
    }

    public Attendant.Status getStatus() {
        return status;
    }

    public void setStatus(Attendant.Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}