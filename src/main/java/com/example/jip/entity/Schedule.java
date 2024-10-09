package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Setter
@Getter
public class Schedule {
    private int id;
    private int block;
    private int week_number;
    private dayOfWeek day_of_week;
    private int slot_number;
    private int teacher_id;
    private int class_id;
    private Time start_time;
    private Time end_time;
    private String description;
    private String event;

    public Schedule() {}

    public Schedule(int id, int block, int week_number, dayOfWeek day_of_week, int slot_number, int teacher_id, int class_id, Time start_time, Time end_time, String description, String event) {
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

    public enum dayOfWeek{
        Monday,Tuesday,Wednesday,Thursday,Friday,Saturday
    }

}
