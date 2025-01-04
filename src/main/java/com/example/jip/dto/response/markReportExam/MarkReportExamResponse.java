package com.example.jip.dto.response.markReportExam;

import com.example.jip.entity.Exam;
import jakarta.persistence.Column;
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
public class MarkReportExamResponse {
    int markRpId;
    BigDecimal kanji;
    BigDecimal bunpou;
    BigDecimal kotoba;
}
