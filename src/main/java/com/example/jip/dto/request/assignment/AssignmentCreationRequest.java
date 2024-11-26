package com.example.jip.dto.request.assignment;

import com.example.jip.dto.TeacherDTO;
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
public class AssignmentCreationRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date created_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end_date;
    String description;
    String content;
    MultipartFile[] imgFile;
    TeacherDTO teacher;
    List<Integer> classIds;

}
