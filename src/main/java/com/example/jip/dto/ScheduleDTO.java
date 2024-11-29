package com.example.jip.dto;

import com.example.jip.configuration.TimeDeserializer;
import com.example.jip.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import java.sql.Date;
import java.sql.Time;

public class ScheduleDTO {
    private int id;
        private Schedule.dayOfWeek dayOfWeek;
        private int classId;
        private Time startTime;
        private Time endTime;
        private String description;
        private String event;
        private Date date;
        private String location;
        private String className;
        private int teacherId;
        private String teacherFullName;

    public ScheduleDTO() {
    }

    public ScheduleDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.date = schedule.getDate();
        this.dayOfWeek = schedule.getDay_of_week();
        this.className = schedule.getClasz() != null ? schedule.getClasz().getName() : null;
        this.location = schedule.getLocation();
        this.startTime = schedule.getStart_time();
        this.teacherFullName = schedule.getClasz() != null && schedule.getClasz().getTeacher() != null
                ? schedule.getClasz().getTeacher().getFullname()
                : null;
        this.endTime = schedule.getEnd_time();
        this.description = schedule.getDescription();
        this.event = schedule.getEvent();
        this.classId = schedule.getClasz() != null ? schedule.getClasz().getId() : null;
        this.teacherId = schedule.getClasz() != null && schedule.getClasz().getTeacher() != null
                ? schedule.getClasz().getTeacher().getId()
                : null;
    }

    public ScheduleDTO(Schedule.dayOfWeek dayOfWeek, int classId, Time startTime, Time endTime,
                       String description, String event, Date date, String location,
                       String className, int teacherId, String teacherFullName) {
        this.dayOfWeek = dayOfWeek;
        this.classId = classId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.event = event;
        this.date = date;
        this.location = location;
        this.className = className;
        this.teacherId = teacherId;
        this.teacherFullName = teacherFullName;
    }


    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
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

}