package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MarkReport {
    private int id;
    private int student_id;
    private BigDecimal attendant_rate;
    private BigDecimal avg_assignment_mark;
    private BigDecimal avg_exam_mark;

    public MarkReport() {}

    public MarkReport(int id, int student_id, BigDecimal attendant_rate, BigDecimal avg_assignment_mark, BigDecimal avg_exam_mark) {
        this.id = id;
        this.student_id = student_id;
        this.attendant_rate = attendant_rate;
        this.avg_assignment_mark = avg_assignment_mark;
        this.avg_exam_mark = avg_exam_mark;
    }
}
