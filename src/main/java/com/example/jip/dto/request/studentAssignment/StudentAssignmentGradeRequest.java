package com.example.jip.dto.request.studentAssignment;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.AssignmentStudent;
import com.example.jip.entity.StudentAssignment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAssignmentGradeRequest {
    BigDecimal mark;
    StudentAssignment.Status status = StudentAssignment.Status.MARKED;
}
