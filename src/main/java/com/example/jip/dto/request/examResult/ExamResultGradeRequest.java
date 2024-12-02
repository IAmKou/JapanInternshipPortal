package com.example.jip.dto.request.examResult;

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
public class ExamResultGradeRequest {
    int studentId; // Or just the student ID if fetching the full Student is not needed
    BigDecimal mark;
}
