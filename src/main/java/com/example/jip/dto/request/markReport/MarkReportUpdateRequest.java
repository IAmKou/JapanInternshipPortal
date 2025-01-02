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
    BigDecimal middle_exam;
    @NonNull
    BigDecimal final_exam;
    @NonNull
    String comment;
    @NonNull
    BigDecimal presentation;
    @NonNull
    BigDecimal scriptPresentation;
}
