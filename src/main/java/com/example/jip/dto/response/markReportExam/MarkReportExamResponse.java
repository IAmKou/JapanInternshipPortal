package com.example.jip.dto.response.markReportExam;

import com.example.jip.entity.Exam;
import jakarta.persistence.Column;
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
public class MarkReportExamResponse {
    int markRpId;
    String examName;
    BigDecimal kanji;
    BigDecimal bunpou;
    BigDecimal kotoba;
}
