package com.example.model;

public class Class {
    private int id;
    private String name;
    private int number_of_student;
    private int teacher_id;

    public Class(){}

    public Class(int id, String name, int number_of_student, int teacher_id) {
        this.id = id;
        this.name = name;
        this.number_of_student = number_of_student;
        this.teacher_id = teacher_id;
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

    public int getNumber_of_student() {
        return number_of_student;
    }

    public void setNumber_of_student(int number_of_student) {
        this.number_of_student = number_of_student;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }
}
