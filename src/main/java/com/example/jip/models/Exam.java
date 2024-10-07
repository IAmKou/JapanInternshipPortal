package com.example.jip.models;

import java.math.BigDecimal;
import java.sql.Date;

public class Exam {
    private int id;
    private int student_id;
    private int block;
    private String exam_name;
    private BigDecimal mark;
    private Date exam_date;

    public Exam(){}

    public Exam(int id, int student_id, int block, String exam_name, BigDecimal mark, Date exam_date) {
        this.id = id;
        this.student_id = student_id;
        this.block = block;
        this.exam_name = exam_name;
        this.mark = mark;
        this.exam_date = exam_date;
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

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public BigDecimal getMark() {
        return mark;
    }

    public void setMark(BigDecimal mark) {
        this.mark = mark;
    }

    public Date getExam_date() {
        return exam_date;
    }

    public void setExam_date(Date exam_date) {
        this.exam_date = exam_date;
    }
}
