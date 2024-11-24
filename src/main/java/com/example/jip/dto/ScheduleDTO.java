package com.example.jip.dto;

import com.example.jip.entity.Lesson;
import com.example.jip.entity.Schedule;

import java.sql.Date;
import java.sql.Time;

public class ScheduleDTO {
    private Date date;
    private Schedule.dayOfWeek dayOfWeek;
    private String lessonName;
    private Time startTime;
    private Time endTime;
    private String description;
    private Lesson lesson;

    public ScheduleDTO() {
    }

    public ScheduleDTO(Schedule schedule) {
        this.date = schedule.getDate();
        this.dayOfWeek = schedule.getDay_of_week();
        this.lessonName = lesson.getName();
        this.startTime = schedule.getStart_time();
        this.endTime = schedule.getEnd_time();
        this.description = schedule.getDescription();
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
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

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
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
}