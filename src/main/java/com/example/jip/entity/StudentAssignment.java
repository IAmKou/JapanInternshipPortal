package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
public class StudentAssignment {
    private int id;
    private int student_id;
    private int assignment_id;
    private BigDecimal mark;
    private String description;
    private String content;
    private Date date;

    public StudentAssignment() {}

    public StudentAssignment(int id, int student_id, int assignment_id, BigDecimal mark, String description, String content, Date date) {
        this.id = id;
        this.student_id = student_id;
        this.assignment_id = assignment_id;
        this.mark = mark;
        this.description = description;
        this.content = content;
        this.date = date;
    }

}
