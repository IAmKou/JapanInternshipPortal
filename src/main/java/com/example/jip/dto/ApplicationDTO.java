package com.example.jip.dto;

import com.example.jip.entity.Application;

import java.util.Date;

public class ApplicationDTO {
    private int id;
    private String name;
    private String category;
    private String content;
    private String img;
    private Date created_date;
    private status status;
    private String reply;
    private Date replied_date;
    private TeacherDTO teacher;
    private StudentDTO student;

    public enum status{
        Pending, Approved, Rejected
    }

    public ApplicationDTO() {}

    public ApplicationDTO(int id, String name, String category, String content, String img, Date created_date, ApplicationDTO.status status, String reply, Date replied_date, TeacherDTO teacher, StudentDTO student) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.content = content;
        this.img = img;
        this.created_date = created_date;
        this.status = status;
        this.reply = reply;
        this.replied_date = replied_date;
        this.teacher = teacher;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public ApplicationDTO.status getStatus() {
        return status;
    }

    public void setStatus(ApplicationDTO.status status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getReplied_date() {
        return replied_date;
    }

    public void setReplied_date(Date replied_date) {
        this.replied_date = replied_date;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }
}
