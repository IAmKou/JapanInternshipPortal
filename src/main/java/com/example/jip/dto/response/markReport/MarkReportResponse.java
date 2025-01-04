package com.example.jip.dto.response.markReport;


import com.example.jip.dto.response.markReportExam.MarkReportExamResponse;
import com.example.jip.entity.MarkReportExam;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReportResponse {
    int id;
    String studentName;
    String studentClass;
    String studentEmail;
    BigDecimal softskill;
    BigDecimal avg_exam_mark;
    BigDecimal middle_exam;
    BigDecimal final_exam;
    BigDecimal scriptPresentation;
    BigDecimal presentation;
    BigDecimal skill;
    BigDecimal attitude;
    BigDecimal final_mark;
    String comment;

}
