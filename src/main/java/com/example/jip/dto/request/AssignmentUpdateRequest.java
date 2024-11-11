package com.example.jip.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentUpdateRequest {
    Date end_date;
    String description;
    String content;
    String img;
}
