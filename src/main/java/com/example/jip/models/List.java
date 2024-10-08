package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class List {
    private int class_id;
    private int student_id;

    public List(){}

    public List(int class_id, int student_id) {
        this.class_id = class_id;
        this.student_id = student_id;
    }

}
