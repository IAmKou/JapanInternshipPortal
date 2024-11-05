package com.example.jip.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MarkReportDTO {
    private int id;
    private int studentId;
    private BigDecimal attendantRate;
    private BigDecimal avgAssignmentMark;
    private BigDecimal avgExamMark;
    private BigDecimal readingMark;
    private BigDecimal listeningMark;
    private BigDecimal speakingMark;
    private BigDecimal totalSkillMarks;
    private BigDecimal courseTotal;

    public MarkReportDTO() {}

    public MarkReportDTO(int id, int studentId, BigDecimal attendantRate, BigDecimal avgAssignmentMark, BigDecimal avgExamMark, BigDecimal readingMark, BigDecimal listeningMark, BigDecimal speakingMark, BigDecimal totalSkillMarks, BigDecimal courseTotal) {
        this.id = id;
        this.studentId = studentId;
        this.attendantRate = attendantRate;
        this.avgAssignmentMark = avgAssignmentMark;
        this.avgExamMark = avgExamMark;
        this.readingMark = readingMark;
        this.listeningMark = listeningMark;
        this.speakingMark = speakingMark;
        this.totalSkillMarks = totalSkillMarks;
        this.courseTotal = courseTotal;
    }
}
