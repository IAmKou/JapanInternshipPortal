package com.example.jip.dto;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Setter
@Getter
public class MarkReportDTO {

    private Integer studentId;
    private String fullname; // Add fullname field
    private BigDecimal softskill;
    private BigDecimal presentation;
    private BigDecimal scriptPresentation;
    private BigDecimal avgExamMark;
    private BigDecimal middleExam;
    private BigDecimal finalExam;
    private BigDecimal skill;
    private BigDecimal attitude;
    private BigDecimal finalMark;
    private String comment;
    private BigDecimal assignment; // New field
    private Integer attendant;

    // Constructor for manager list
    public MarkReportDTO(Integer studentId, String fullname, BigDecimal softskill, BigDecimal avgExamMark,
                         BigDecimal middleExam, BigDecimal finalExam, BigDecimal attitude, BigDecimal finalMark, String comment,BigDecimal skill,BigDecimal presentation,BigDecimal scriptPresentation ) {
        this.studentId = studentId;
        this.fullname = fullname;
        this.softskill = softskill;
        this.avgExamMark = avgExamMark;
        this.middleExam = middleExam;
        this.finalExam = finalExam;
        this.attitude = attitude;
        this.finalMark = finalMark;
        this.comment = comment;
        this.skill = skill;
        this.presentation = presentation;
        this.scriptPresentation = scriptPresentation;
    }
}