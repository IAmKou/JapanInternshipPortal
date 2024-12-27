package com.example.jip.dto.request.studentAssignment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAssignmentFileDeleteRequest {

    String fileUrl;
    int studentAssignmentId;

}
