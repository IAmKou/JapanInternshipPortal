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

    @Column(name="student_id")
    private int studentId; // Giữ nguyên tên thuộc tính là student_id
    private BigDecimal attendant_rate;
    private BigDecimal avg_assignment_mark;
    private BigDecimal avg_exam_mark;
    private BigDecimal reading_mark; // Điểm Reading
    private BigDecimal listening_mark; // Điểm Listening
    private BigDecimal speaking_mark; // Điểm Speaking

    public MarkReport() {}

    public MarkReport(int id, int studentId, BigDecimal attendant_rate, BigDecimal avg_assignment_mark, BigDecimal avg_exam_mark, BigDecimal reading_mark, BigDecimal listening_mark, BigDecimal speaking_mark) {
        this.id = id;
        this.studentId = studentId;
        this.attendant_rate = attendant_rate;
        this.avg_assignment_mark = avg_assignment_mark;
        this.avg_exam_mark = avg_exam_mark;
        this.reading_mark = reading_mark;
        this.listening_mark = listening_mark;
        this.speaking_mark = speaking_mark;
    }
    // Tính tổng điểm cho Reading, Listening, Speaking
    public BigDecimal getTotalSkillMarks() {
        return reading_mark.add(listening_mark).add(speaking_mark);
    }

    // Tính tổng điểm của tất cả các thành phần
    public BigDecimal getCourseTotal() {
        return avg_assignment_mark.add(avg_exam_mark).add(getTotalSkillMarks()).add(attendant_rate);
    }
}
