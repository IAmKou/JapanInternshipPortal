package com.example.jip.dto.request.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NonNull
    Date created_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NonNull
    Date end_date;
    String description;
    String content;
    MultipartFile[] imgFile;
    List<Integer> classIds;

}
