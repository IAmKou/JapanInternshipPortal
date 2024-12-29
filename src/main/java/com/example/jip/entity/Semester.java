package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "semester")
@Getter
@Setter
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_time")
    private Date start_time;

    @Column(name = "end_time")
    private Date end_time;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private status status;

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    private List<Class> classes;

    public enum status{
        Active, Inactive
    }

    public Semester(int id, String name, Date start_time, Date end_time, Semester.status status) {
        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
    }
    public Semester() {}
}
