package com.example.jip.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentCreationRequest {
    Date created_date = new Date(System.currentTimeMillis());
    Date end_date;
    String description;
    String content;
    int teacher_id;
    String img;
}
