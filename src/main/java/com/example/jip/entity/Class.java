package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Class {
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
