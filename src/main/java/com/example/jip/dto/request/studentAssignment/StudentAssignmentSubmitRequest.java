package com.example.jip.dto.request.studentAssignment;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAssignmentSubmitRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date date;
    @NonNull
    String description;
    @NonNull
    String content;
    StudentDTO student;
    AssignmentResponse assignment;
}
