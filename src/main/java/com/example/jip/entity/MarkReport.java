package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "Mark_report")
public class MarkReport {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(precision = 5, scale = 2)
    private BigDecimal softskill;

    @Column(precision = 5, scale = 2)
    private BigDecimal avg_exam_mark;

    @Column(precision = 5, scale = 2)
    private BigDecimal middle_exam;

    @Column(precision = 5, scale = 2)
    private BigDecimal final_exam;

    @Column(precision = 5, scale = 2)
    private BigDecimal attitude;

    @Column(precision = 5, scale = 2)
    private BigDecimal final_mark;

    private String comment;
}
