package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class Attendant {
    private int id;
    private int student_id;
    private int schedule_id;
    private String status;
    private Date date;
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
