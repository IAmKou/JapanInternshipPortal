package com.example.jip.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentResponse {
     Date created_date;
     Date end_date;
     String description;
     int teacher_id;
     String img;
}
