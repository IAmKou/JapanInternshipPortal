package com.example.jip.models;

import java.sql.Time;

public class Schedule {
    private int id;
    private int block;
    private int week_number;
    private String day_of_week;
    private int slot_number;
    private int teacher_id;
    private int class_id;
    private Time start_time;
    private Time end_time;
    private String description;
    private String event;

    public Schedule() {}

    public Schedule(int id, int block, int week_number, String day_of_week, int slot_number, int teacher_id, int class_id, Time start_time, Time end_time, String description, String event) {
        this.id = id;
        this.block = block;
        this.week_number = week_number;
        this.day_of_week = day_of_week;
        this.slot_number = slot_number;
        this.teacher_id = teacher_id;
        this.class_id = class_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getWeek_number() {
        return week_number;
    }

    public void setWeek_number(int week_number) {
        this.week_number = week_number;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public int getSlot_number() {
        return slot_number;
    }

    public void setSlot_number(int slot_number) {
        this.slot_number = slot_number;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
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
