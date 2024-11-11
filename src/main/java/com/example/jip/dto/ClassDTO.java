package com.example.jip.dto;

import java.util.List;

public class ClassDTO {
    private String name;
    private int numberOfStudents;
    private TeacherDTO teacher;
    private List<Integer> studentIds; // Add this field

    public ClassDTO() {}

    public ClassDTO(String name, int numberOfStudents, TeacherDTO teacher, List<Integer> studentIds) {
        this.name = name;
        this.numberOfStudents = numberOfStudents;
        this.teacher = teacher;
        this.studentIds = studentIds;
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
}
