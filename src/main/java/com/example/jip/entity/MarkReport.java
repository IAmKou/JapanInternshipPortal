package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="mark_report")
@Setter
@Getter
public class MarkReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Tham chiếu tới bảng Student (Khóa ngoại)
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private Student student;

    @Column(name="attendance_rate")
    private BigDecimal attendance_rate; // Tỷ lệ tham gia

    @Column(name="avg_assignment_mark")
    private BigDecimal avg_assignment_mark; // Điểm bài tập

    @Column(name="avg_exam_mark")
    private BigDecimal avg_exam_mark; // Điểm thi

    // Constructor không tham số
    public MarkReport() {}

}
