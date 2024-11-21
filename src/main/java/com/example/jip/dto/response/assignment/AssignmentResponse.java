package com.example.jip.dto.response.assignment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentResponse {
    String description;
    String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date created_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end_date;
    List<String> classes; // List of class names
    List<String> files;
}
