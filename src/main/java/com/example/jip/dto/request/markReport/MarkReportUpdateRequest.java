package com.example.jip.dto.request.markReport;

import com.example.jip.dto.request.markReportExam.MarkReportExamUpdateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReportUpdateRequest {
    BigDecimal middle_exam;
    BigDecimal final_exam;
    BigDecimal presentation;
    BigDecimal scriptPresentation;
    String comment;
    List<MarkReportExamUpdateRequest> exams;
}

