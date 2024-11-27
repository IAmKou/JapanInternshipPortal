package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
@Entity
@Table(name="Schedule")
@Setter
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="Date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private dayOfWeek day_of_week;

    @Column(name="location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="class_id")
    private Class clasz;

    @Column(name = "start_time")
    private Time start_time;

    @Column(name = "end_time")
    private Time end_time;

    @Column(name = "description")
    private String description;

    @Column(name = "event")
    private String event;

    public Schedule() {}

    public Schedule(int id, Date date, dayOfWeek day_of_week, String location, Class clasz, Time start_time, Time end_time, String description, String event) {
        this.id = id;
        this.date = date;
        this.day_of_week = day_of_week;
        this.location = location;
        this.clasz = clasz;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
        this.event = event;
    }

    public enum dayOfWeek{
        Monday,Tuesday,Wednesday,Thursday,Friday,Saturday
    }

}
