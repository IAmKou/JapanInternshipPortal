package com.example.jip.dto.request.markReport;

import com.example.jip.entity.MarkReportExam;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReportImportRequest {
    String name;
    @NonNull
    String email;
    BigDecimal presentation;
    BigDecimal scriptPresentation;
    BigDecimal middle_exam;
    BigDecimal final_exam;
    String comment;


}
