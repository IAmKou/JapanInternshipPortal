package com.example.jip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int number_of_student;
    private int teacher_id;

    public Class(){}

    public Class(int id, String name, int number_of_student, int teacher_id) {
        this.id = id;
        this.name = name;
        this.number_of_student = number_of_student;
        this.teacher_id = teacher_id;
    }

}
