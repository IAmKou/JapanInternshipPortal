package com.example.jip.models;

import java.sql.Date;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }
}
