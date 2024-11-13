package com.example.jip.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAssignmentResponse {
    private int student_id;
    private int assignment_id;
    private BigDecimal mark;
    private String description;
    private String content;
    private Date date;
}
