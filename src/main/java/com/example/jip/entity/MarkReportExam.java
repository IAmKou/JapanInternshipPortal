package com.example.jip.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

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
    @JoinColumn(name = "mark_rp_id")
    private MarkReport markReport;

    @ManyToOne
    @MapsId("examId") // Maps the examId field of the composite key
    @JoinColumn(name = "exam_id")
    private Exam exam;
    public MarkReportExam(MarkReport markReport, Exam exam) {
        this.id = new MarkReportExamId(markReport.getId(), exam.getId());
        this.markReport = markReport;
        this.exam = exam;
    }
}