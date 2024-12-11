package com.example.jip.dto.request.exam;

import com.example.jip.dto.TeacherDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamUpdateRequest {

    String exam_name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date exam_date;
    String content;
}