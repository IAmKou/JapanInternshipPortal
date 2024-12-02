package com.example.jip.dto.response.studentAssignment;

import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
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
public class StudentAssignmentResponse {
    int id;
    BigDecimal mark;
    String description;
    String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date date;
    String status;
    int studentId;
    int assignmentId;
    String assignmentName;

}
