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
    private BigDecimal reading_mark; // Điểm Reading
    private BigDecimal listening_mark; // Điểm Listening
    private BigDecimal speaking_mark; // Điểm Speaking

    private String comment; // Bình luận

    // Constructor không tham số
    public MarkReport() {}

    // Constructor đầy đủ
    public MarkReport(int id, Student student, BigDecimal attendance_rate, BigDecimal avg_assignment_mark, BigDecimal avg_exam_mark, BigDecimal reading_mark, BigDecimal listening_mark, BigDecimal speaking_mark) {
        this.id = id;
        this.student = student;
        this.attendance_rate = attendance_rate;
        this.avg_assignment_mark = avg_assignment_mark;
        this.avg_exam_mark = avg_exam_mark;
        this.reading_mark = reading_mark;
        this.listening_mark = listening_mark;
        this.speaking_mark = speaking_mark;
    }
}
