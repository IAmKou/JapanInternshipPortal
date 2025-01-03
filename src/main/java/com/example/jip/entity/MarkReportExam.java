package com.example.jip.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "mark_report_exam")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReportExam {
    @EmbeddedId
    private MarkReportExamId id;

    @ManyToOne
    @MapsId("markReportId") // Maps the markReportId field of the composite key
    @JoinColumn(name = "mark_report_id", nullable = false) // Corrected column name
    private MarkReport markReport;

    @ManyToOne
    @MapsId("examId") // Maps the examId field of the composite key
    @JoinColumn(name = "exam_id", nullable = false) // Corrected column name
    private Exam exam;

    @Column(name = "kanji")
    BigDecimal kanji;
    @Column(name = "bunpou")
    BigDecimal bunpou;
    @Column(name = "kotoba")
    BigDecimal kotoba;

    public MarkReportExam(MarkReport markReport, Exam exam) {
        if (markReport == null || markReport.getId() == 0) {
            throw new IllegalArgumentException("MarkReport must be saved and have a valid ID before creating a MarkReportExam.");
        }
        if (exam == null || exam.getId() == 0) {
            throw new IllegalArgumentException("Exam must be saved and have a valid ID before creating a MarkReportExam.");
        }
        this.id = new MarkReportExamId(markReport.getId(), exam.getId());
        this.markReport = markReport;
        this.exam = exam;
    }
}