package com.example.jip.dto;

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
    private String semesterName;

    public ClassDTO(Class clasz) {
        this.name = clasz.getName();
        this.id = clasz.getId();
        this.numberOfStudents = clasz.getNumber_of_student();
        this.teacherName = clasz.getTeacher().getFullname();
        this.semesterId = (clasz.getSemester() != null) ? clasz.getSemester().getId() : 0;
    }
}
