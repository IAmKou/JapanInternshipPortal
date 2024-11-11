package com.example.jip.dto;

import java.util.Date;

public class MaterialDTO {
    private int id;
    private String title;
    private String content;
    private String img;
    private Date created_date;
    private TeacherDTO teacher;

    public MaterialDTO() {}

    public MaterialDTO(int id, String title, String content, String img, Date created_date, TeacherDTO teacher) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.img = img;
        this.created_date = created_date;
        this.teacher = teacher;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }
}
