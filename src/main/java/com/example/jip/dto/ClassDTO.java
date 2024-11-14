package com.example.jip.dto;

import com.example.jip.entity.Class;
import java.util.List;


public class ClassDTO {
    private String name;
    private int id;
    private int numberOfStudents;
    private TeacherDTO teacher;
    private List<Integer> studentIds;
    private String teacherName;

    public ClassDTO() {}

    public ClassDTO(String name, int numberOfStudents, TeacherDTO teacher, List<Integer> studentIds) {
        this.name = name;
        this.numberOfStudents = numberOfStudents;
        this.teacher = teacher;
        this.studentIds = studentIds;
    }

    public ClassDTO(Class clasz) {
        this.name = clasz.getName();
        this.id = clasz.getId();
        this.numberOfStudents = clasz.getNumber_of_student();
        this.teacherName = clasz.getTeacher().getFullname();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }

    public List<Integer> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
