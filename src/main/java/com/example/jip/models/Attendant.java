package com.example.jip.models;

import java.sql.Date;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
