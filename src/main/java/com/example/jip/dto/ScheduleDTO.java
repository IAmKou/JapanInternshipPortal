package com.example.jip.dto;

import com.example.jip.entity.Schedule;

import java.sql.Date;
import java.sql.Time;

public class ScheduleDTO {
    private Date date;
    private Schedule.dayOfWeek dayOfWeek;
    private String className;
    private Time startTime;
    private Time endTime;
    private String description;
    private Class clasz;

    public ScheduleDTO() {
    }

    public ScheduleDTO(Schedule schedule) {
        this.date = schedule.getDate();
        this.dayOfWeek = schedule.getDay_of_week();
        this.className = schedule.getClass().getName();
        this.startTime = schedule.getStart_time();
        this.endTime = schedule.getEnd_time();
        this.description = schedule.getDescription();
    }


    public Date getDate() {
        return date;
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