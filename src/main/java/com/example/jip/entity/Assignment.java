package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class Assignment {
    private int id;
    private Date created_date;
    private Date end_date;
    private String description;
    private int teacher_id;
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
