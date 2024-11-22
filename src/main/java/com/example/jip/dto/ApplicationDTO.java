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
    private Status status; // Đổi tên enum
    private String reply;
    private Date replied_date;
    private TeacherDTO teacher;
    private StudentDTO student;

    public static enum Status {
        Pending, Approved, Rejected
    }

    // Cập nhật phương thức toDTOStatus để chuyển từ entity status sang DTO status
    public static ApplicationDTO.Status toDTOStatus(Application.Status entityStatus) { // Chú ý kiểu của entityStatus
        if (entityStatus == null) return null;
        switch (entityStatus) {
            case Pending:
                return ApplicationDTO.Status.Pending;
            case Approved:
                return ApplicationDTO.Status.Approved;
            case Rejected:
                return ApplicationDTO.Status.Rejected;
            default:
                throw new IllegalArgumentException("Unknown status: " + entityStatus);
        }
    }

    // Getters and setters...
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
