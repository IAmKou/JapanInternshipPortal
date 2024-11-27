package com.example.jip.dto.response.assignment;

import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
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
    int id;
    String description;
    String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date created_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end_date;
    String folder;
    Teacher teacher; // Teacher who created this assignment
    Student student;
    List<String> classes; // List of class names
    List<String> files;
}
