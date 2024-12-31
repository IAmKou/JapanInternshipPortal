package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Entity
@Table(name = "Attendant")
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

    @ManyToOne
    @JoinColumn(name = "curriculum_id", referencedColumnName = "id", nullable = false)
    private Curriculum curriculum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Date date;

    public enum Status {
        PRESENT,
        ABSENT,
        LATE,
        PERMITTED
    }

    public Attendant(Integer id, Student student, Schedule schedule, Curriculum curriculum, Status status, Date date) {
        this.id = id;
        this.student = student;
        this.schedule = schedule;
        this.curriculum = curriculum;
        this.status = status;
        this.date = date;
    }

    public Attendant() {
    }
}
