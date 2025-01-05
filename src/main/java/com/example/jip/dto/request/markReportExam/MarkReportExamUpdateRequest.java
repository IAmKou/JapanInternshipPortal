package com.example.jip.dto.request.markReportExam;

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
public class MarkReportExamUpdateRequest {
    int markRpId;
    String examName;
    BigDecimal kanji;
    BigDecimal bunpou;
    BigDecimal kotoba;
}
