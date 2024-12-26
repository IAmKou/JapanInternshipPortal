package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name="holiday")
@Getter
@Setter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date")
    private Date date;

    @Column(name= "name")
    private String name;

    public Holiday(int id, Date date, String name) {
        this.id = id;
        this.date = date;
        this.name = name;
    }

    public Holiday() {}
}
