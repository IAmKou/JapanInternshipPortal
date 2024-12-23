package com.example.jip.dto.response.assignment;

import com.example.jip.entity.Teacher;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentResponse {
    int id;
    String description;
    String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    Date created_date;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    Date end_date;
    String folder;
    String status;
    Teacher teacher; // Teacher who created this assignment
    List<String> classes; // List of class names
    List<String> files;
}
