package com.example.jip.dto.response.exam;

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
public class ExamResponse {
    int markId;
    int examId;
    String title;
    BigDecimal kanji;
    BigDecimal bunpou;
    BigDecimal kotoba;
}
