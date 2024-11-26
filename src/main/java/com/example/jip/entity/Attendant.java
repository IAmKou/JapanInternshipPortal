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
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name="Date")
    private Date date;

    @Column(name = "note")
    private String note;

    public Attendant(){}

    public Attendant(int id, Student student, Schedule schedule, Status status, Date date, String note) {
        this.id = id;
        this.student = student;
        this.schedule = schedule;
        this.status = status;
        this.date = date;
        this.note = note;
    }

    public enum Status {
        Present, Late, Absent, Permitted_Absence;

        @Override
        public String toString() {
            // Replace underscores with spaces
            return this.name().replace("_", " ");
        }
    }

}
