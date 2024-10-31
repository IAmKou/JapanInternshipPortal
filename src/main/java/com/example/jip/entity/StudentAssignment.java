package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
@Entity
@Table(name = "StudentAssignment")
@Getter
@Setter
public class StudentAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "student_id")
    private int student_id;
    @Column(name = "assignment_id")
    private int assignment_id;
    @Column(name = "mark")
    private BigDecimal mark;
    @Column(name = "description")
    private String description;
    @Column(name = "content")
    private String content;
    @Column(name = "date", nullable = false)
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
