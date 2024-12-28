package com.example.jip.dto;

import com.example.jip.entity.Assignment;
import com.example.jip.entity.Class;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ClassDTO {
    private String name;
    private int id;
    private int numberOfStudents;
    private TeacherDTO teacher;
    private List<Integer> studentIds;
    private String teacherName;
    private List<Integer> assignmentIds;
    private int semesterId;



    public ClassDTO(String name, int numberOfStudents, TeacherDTO teacher, List<Integer> studentIds, int semesterId) {
        this.name = name;
        this.numberOfStudents = numberOfStudents;
        this.teacher = teacher;
        this.studentIds = studentIds;
        this.semesterId = semesterId;
    }

    public ClassDTO(Class clasz) {
        this.name = clasz.getName();
        this.id = clasz.getId();
        this.numberOfStudents = clasz.getNumber_of_student();
        this.teacherName = clasz.getTeacher().getFullname();
        this.semesterId = clasz.getSemester().getId();
    }

}
