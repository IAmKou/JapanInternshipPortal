package com.example.jip.dto.request;

import com.example.jip.dto.ClassDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentUpdateRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end_date;
    String description;
    String content;
    MultipartFile imgFile;
    ClassDTO classEntity;
}
