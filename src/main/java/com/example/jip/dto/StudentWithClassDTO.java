package com.example.jip.dto;

import com.example.jip.entity.Student;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentWithClassDTO {
    private int studentId;
    private String studentName;
    private Student.Gender gender;
    private String img;
    private String className;

    public StudentWithClassDTO(int studentId, String studentName, Student.Gender gender, String img, String className) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.img = img;
        this.className = className;
    }

    public StudentWithClassDTO(int studentId, String studentName, String img, String className) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.img = img;
        this.className = className;
    }
}
