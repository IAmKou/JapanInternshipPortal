package com.example.jip.dto.request.studentAssignment;

import com.example.jip.entity.StudentAssignment;
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
public class StudentAssignmentGradeRequest {
    BigDecimal mark;
    StudentAssignment.Status status = StudentAssignment.Status.MARKED;
}
