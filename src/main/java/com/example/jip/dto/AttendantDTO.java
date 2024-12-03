package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.sql.Date;

public class AttendantDTO {
    private int attendantId;
    private Attendant.Status status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private int studentId;
    private int scheduleId;
    private String studentName;
    private boolean mark;
    private String img;


    public AttendantDTO() {}

    public AttendantDTO(Attendant attendant) {
        this.attendantId = attendant.getId();
        this.status = attendant.getStatus();
        this.date = attendant.getDate();
        this.studentId = attendant.getStudent().getId();
        this.scheduleId = attendant.getSchedule().getId();
        this.img = attendant.getStudent().getImg();
        this.mark = attendant.getStudent().isMark();
        this.studentName = attendant.getStudent().getFullname();
    }

    public AttendantDTO(int attendantId, Attendant.Status status, Date date, int studentId, int scheduleId, String studentName, boolean mark, String img) {
        this.attendantId = attendantId;
        this.status = status;
        this.date = date;
        this.studentId = studentId;
        this.scheduleId = scheduleId;
        this.studentName = studentName;
        this.mark = mark;
        this.img = img;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    @Override
    public String toString() {
        return "AttendantDTO {  status=" + status
                + ", date=" + date + ", studentId=" + studentId + "}";
    }
}
