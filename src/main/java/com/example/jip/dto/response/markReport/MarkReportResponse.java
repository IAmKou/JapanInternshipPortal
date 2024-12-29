package com.example.jip.dto.response.markReport;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
    BigDecimal skill;
    BigDecimal attitude;
    BigDecimal final_mark;
    String comment;
}
