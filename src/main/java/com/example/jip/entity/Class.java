package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "Class")
@Setter
@Getter
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number_of_student", nullable = false)
    private int number_of_student;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;


    public Class(){}

    public Class(int id, String name, int number_of_student, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.number_of_student = number_of_student;
        this.teacher = teacher;
    }

}
