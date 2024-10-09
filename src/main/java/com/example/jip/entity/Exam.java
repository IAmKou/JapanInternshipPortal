package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Setter
@Getter
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

}
