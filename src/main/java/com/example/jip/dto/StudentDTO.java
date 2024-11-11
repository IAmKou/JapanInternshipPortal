package com.example.jip.dto;

import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;

import java.sql.Date;

public class StudentDTO {
    private int id;
    private String fullname;
    private String jname;
    private Student.Gender gender;
    private String img;



    public StudentDTO(Student student) {
        this.id = student.getId();
        this.fullname = student.getFullname();
        this.jname = student.getJapanname();
        this.gender = student.getGender();
        this.img = student.getImg();
    }

    public StudentDTO(int id, String fullname, String jname, Student.Gender gender, String img) {
        this.id = id;
        this.fullname = fullname;
        this.jname = jname;
        this.gender = gender;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getJname() {
        return jname;
    }

    public void setJname(String jname) {
        this.jname = jname;
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
}
