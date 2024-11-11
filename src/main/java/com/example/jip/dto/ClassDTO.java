package com.example.jip.dto;

public class ClassDTO {
    private int id;
    private String name;
    private int numberOfStudents;
    private TeacherDTO teacher;

    public ClassDTO() {}



    public ClassDTO(int id, String name, int numberOfStudents, TeacherDTO teacher) {
        this.id = id;
        this.name = name;
        this.numberOfStudents = numberOfStudents;
        this.teacher = teacher;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
