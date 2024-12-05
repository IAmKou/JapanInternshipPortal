package com.example.jip.dto.response.assignmentClass;


import com.example.jip.entity.AssignmentClass;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentClassResponse {
    int assignmentId;
    int classId;
    String className;

}
