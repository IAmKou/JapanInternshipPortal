package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Setter
@Getter
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name =  "date_created", nullable = false)
    private Date created_date;
    @Column(name =  "end_date", nullable = false)
    private Date end_date;
    @Column(name = "description")
    private String description;
    @Column(name = "teacher_id")
    private int teacher_id;
    @Column(name = "img")
    private String img;

    private int class_id;



    public Assignment() {}

    public Assignment(int id, Date created_date, Date end_date, String description, int teacher_id, String img, int class_id) {
        this.id = id;
        this.created_date = created_date;
        this.end_date = end_date;
        this.description = description;
        this.teacher_id = teacher_id;
        this.img = img;
        this.class_id = class_id;
    }

}
