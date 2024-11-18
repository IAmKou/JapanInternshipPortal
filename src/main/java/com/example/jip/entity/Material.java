package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "img")
    private String img;

    @Column(name =  "date_created", nullable = false)
    private Date created_date;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;


    public Material() {}

    public Material(int id, String title, String content, String img, Date created_date, Teacher teacher) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.img = img;
        this.created_date = created_date;
        this.teacher = teacher;
    }
}