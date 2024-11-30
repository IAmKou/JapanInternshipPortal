package com.example.jip.dto;

import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;

import java.sql.Date;
import java.sql.Time;

public class StudentScheduleDTO {
    private int scheduleId;
    private String className;
    private String teacherName;
    private String location;
    private Schedule.dayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private Attendant.Status attendanceStatus;
    private Date date;
    private String description;
    private String event;

    public StudentScheduleDTO(int scheduleId, String className, String teacherName, String location, Schedule.dayOfWeek dayOfWeek, Time startTime, Time endTime, Attendant.Status attendanceStatus, Date date, String description, String event) {
        this.scheduleId = scheduleId;
        this.className = className;
        this.teacherName = teacherName;
        this.location = location;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendanceStatus = attendanceStatus;
        this.date = date;
        this.description = description;
        this.event = event;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Schedule.dayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Schedule.dayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Attendant.Status getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(Attendant.Status attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
