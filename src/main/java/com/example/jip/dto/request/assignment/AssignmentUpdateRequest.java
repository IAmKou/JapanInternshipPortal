package com.example.jip.dto.request.assignment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentUpdateRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end_date;
    String description;
    String content;
    MultipartFile[] imgFile;
    List<Integer> classIds;
    // List name files of this assignment
}
