package com.example.jip.dto;

import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;

import java.sql.Date;

public class StudentDTO {
    private String fullname;
    private String jname;
    private Date dob;
    private String passport;
    private String email;
    private String phoneNumber;
    private Student.Gender gender;
    private String img;



    public StudentDTO(Student student) {
        this.fullname = student.getFullname();
        this.jname = student.getJapanname();
        this.dob = student.getDob();
        this.passport = student.getPassport();
        this.email = student.getEmail();
        this.phoneNumber = student.getPhoneNumber();
        this.gender = student.getGender();
        this.img = student.getImg();
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
