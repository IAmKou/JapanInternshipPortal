package com.example.jip.dto;


import com.example.jip.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.sql.Date;
import java.sql.Time;

public class ScheduleDTO {
    private int id;
    private String date;
    private Schedule.dayOfWeek dayOfWeek;
    private String class_name;
    private String color;
    private String room;
    private String activity;
    private String semesterName;
    private Schedule.status status;
    private int semesterId;
    private Date startTime;
    private Date endTime;
    private int classId;

    public ScheduleDTO() {
    }

    public ScheduleDTO(int id, String date, Schedule.dayOfWeek dayOfWeek, String class_name, String color, String room, String activity, String semesterName, Schedule.status status, int semesterId, Date startTime, Date endTime) {
        this.id = id;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.class_name = class_name;
        this.color = color;
        this.room = room;
        this.activity = activity;
        this.semesterName = semesterName;
        this.status = status;
        this.semesterId = semesterId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ScheduleDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.date = String.valueOf(schedule.getDate());
        this.dayOfWeek = schedule.getDay_of_week();
        this.class_name = (schedule.getClasz() != null) ? schedule.getClasz().getName() : "No Class Assigned";
        this.room = schedule.getRoom();
        this.color = (schedule.getColor() != null) ? schedule.getColor() : "No Color";
        this.activity = schedule.getActivity();
        this.semesterName = (schedule.getSemester() != null) ? schedule.getSemester().getName() : "No Semester Assigned";
        this.status = schedule.getStatus();
        this.semesterId = schedule.getSemester().getId();
        this.startTime = schedule.getSemester().getStart_time();
        this.endTime = schedule.getSemester().getEnd_time();
        this.classId = (schedule.getClasz() != null) ? schedule.getClasz().getId() : 0;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Schedule.dayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDayOfWeek(Schedule.dayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Schedule.status getStatus() {
        return status;
    }

    public void setStatus(Schedule.status status) {
        this.status = status;
    }
}