package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
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

    private BigDecimal attendance_rate; // Tỷ lệ tham gia
    private BigDecimal avg_assignment_mark; // Điểm bài tập
    private BigDecimal avg_exam_mark; // Điểm thi

    // Constructor không tham số
    public MarkReport() {}

}
