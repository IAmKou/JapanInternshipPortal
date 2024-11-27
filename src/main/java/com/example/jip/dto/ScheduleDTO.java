package com.example.jip.dto;

import com.example.jip.entity.Schedule;

import java.sql.Date;
import java.sql.Time;

public class ScheduleDTO {
    private int id;
    private Date date;
    private Schedule.dayOfWeek dayOfWeek;
    private String className;
    private String location;
    private Time startTime;
    private Time endTime;
    private String description;
    private Class clasz;
    private String event;
    private String teacherName;

    public ScheduleDTO() {
    }

    public ScheduleDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.date = schedule.getDate();
        this.dayOfWeek = schedule.getDay_of_week();
        this.className = schedule.getClasz() != null ? schedule.getClasz().getName() : null;
        this.location = schedule.getLocation();
        this.startTime = schedule.getStart_time();
        this.teacherName = schedule.getClasz() != null && schedule.getClasz().getTeacher() != null
                ? schedule.getClasz().getTeacher().getFullname()
                : null;
        this.endTime = schedule.getEnd_time();
        this.description = schedule.getDescription();
        this.event = schedule.getEvent();
    }

    public ScheduleDTO(int id,Date date, Schedule.dayOfWeek dayOfWeek, String className, String location, Time startTime, Time endTime, String description, String event, String teacherName) {
        this.id = id;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.className = className;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.event = event;
        this.teacherName = teacherName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        teacherName = teacherName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class getClasz() {
        return clasz;
    }

    public void setClasz(Class clasz) {
        this.clasz = clasz;
    }
}