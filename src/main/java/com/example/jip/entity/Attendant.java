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


    private int student_id;
    private int schedule_id;


    private String status;

    @Column(name="Date")
    private Date date;

    @Column(name = "note")
    private String note;

    public Attendant(){}

    public Attendant(int id, int student_id, int schedule_id, String status, Date date, String note) {
        this.id = id;
        this.student_id = student_id;
        this.schedule_id = schedule_id;
        this.status = status;
        this.date = date;
        this.note = note;
    }

}
