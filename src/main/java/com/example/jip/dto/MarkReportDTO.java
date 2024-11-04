package com.example.jip.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MarkReportDTO {
    private int id;
    private int student_id; // Đổi thành student_id để khớp
    private BigDecimal attendantRate;
    private BigDecimal avgAssignmentMark;
    private BigDecimal avgExamMark;
    private BigDecimal readingMark; // Thêm thuộc tính này
    private BigDecimal listeningMark; // Thêm thuộc tính này
    private BigDecimal speakingMark; // Thêm thuộc tính này

    public MarkReportDTO() {}

    public MarkReportDTO(int id, int student_id, BigDecimal attendantRate, BigDecimal avgAssignmentMark, BigDecimal avgExamMark, BigDecimal readingMark, BigDecimal listeningMark, BigDecimal speakingMark) {
        this.id = id;
        this.student_id = student_id; // Đảm bảo khớp
        this.attendantRate = attendantRate;
        this.avgAssignmentMark = avgAssignmentMark;
        this.avgExamMark = avgExamMark;
        this.readingMark = readingMark;
        this.listeningMark = listeningMark;
        this.speakingMark = speakingMark;
    }
}
