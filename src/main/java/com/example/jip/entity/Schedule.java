package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Date;
import java.sql.Time;
@Entity
@Table(name="schedule")
@Setter
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private dayOfWeek day_of_week;

    @Column(name="room")
    private String room;

    @Column(name="color")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="class_id")
    private Class clasz;

    @Column(name = "activity")
    private String activity;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private status status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="semester_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Semester semester;

    public Schedule() {}

    public Schedule(int id, Date date, dayOfWeek day_of_week, String room, String color, Class clasz, String activity, Schedule.status status, Semester semester) {
        this.id = id;
        this.date = date;
        this.day_of_week = day_of_week;
        this.room = room;
        this.color = color;
        this.clasz = clasz;
        this.activity = activity;
        this.status = status;
        this.semester = semester;
    }

    public enum dayOfWeek{
        Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday
    }

    public enum status{
        Draft, Published
    }

}