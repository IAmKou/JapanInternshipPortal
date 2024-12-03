package com.example.jip.dto.response.exam;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResponse {
    int id;
    String exam_name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date exam_date;
    String content;
    int teacherId;
}