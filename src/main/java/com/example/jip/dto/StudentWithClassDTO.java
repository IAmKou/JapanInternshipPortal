package com.example.jip.dto;

import com.example.jip.entity.Student;

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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Student.Gender getGender() {
        return gender;
    }

    public void setGender(Student.Gender gender) {
        this.gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
