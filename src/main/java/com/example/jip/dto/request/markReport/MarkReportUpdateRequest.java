package com.example.jip.dto.request.markReport;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReportUpdateRequest {
    @NonNull
    BigDecimal softskill;
    @NonNull
    BigDecimal avg_exam_mark;
    @NonNull
    BigDecimal middle_exam;
    @NonNull
    BigDecimal final_exam;
    @NonNull
    String comment;
}
