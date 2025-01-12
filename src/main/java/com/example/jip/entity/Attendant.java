package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "attendant")
@Setter
@Getter
public class Attendant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Status status;

    @Column(nullable = false)
    private Date date;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(name = "is_finalized", nullable = false)
    private Boolean isFinalized;

    public enum Status {
        PRESENT,
        ABSENT,
        LATE,
        PERMITTED
    }

    public Attendant(Integer id, Student student, Schedule schedule, Status status, Date date, Time startTime, Time endTime, Boolean isFinalized) {
        this.id = id;
        this.student = student;
        this.schedule = schedule;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFinalized = isFinalized;
    }

    public Attendant() {
    }
}
