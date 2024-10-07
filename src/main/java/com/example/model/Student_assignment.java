package com.example.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Student_assignment {
    private int id;
    private int student_id;
    private int assignment_id;
    private BigDecimal mark;
    private String description;
    private String content;
    private Date date;

    public Student_assignment() {}

    public Student_assignment(int id, int student_id, int assignment_id, BigDecimal mark, String description, String content, Date date) {
        this.id = id;
        this.student_id = student_id;
        this.assignment_id = assignment_id;
        this.mark = mark;
        this.description = description;
        this.content = content;
        this.date = date;
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

    public int getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(int assignment_id) {
        this.assignment_id = assignment_id;
    }

    public BigDecimal getMark() {
        return mark;
    }

    public void setMark(BigDecimal mark) {
        this.mark = mark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
