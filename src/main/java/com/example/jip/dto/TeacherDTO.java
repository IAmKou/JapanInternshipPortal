package com.example.jip.dto;

import com.example.jip.entity.Account;
import com.example.jip.entity.Teacher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherDTO {
    private int id;
    private Account account;
    private String fullname;
    private String jname;
    private String email;
    private String phoneNumber;
    private Teacher.gender gender;
    private String img;



    public TeacherDTO(Teacher teacher) {
        this.id = teacher.getId();
        this.account = teacher.getAccount();
        this.fullname = teacher.getFullname();
        this.jname = teacher.getJname();
        this.email = teacher.getEmail();
        this.phoneNumber = teacher.getPhoneNumber();
        this.gender = teacher.getGender();
        this.img = teacher.getImg();
    }

    public TeacherDTO(int id, String fullname, String jname, String img){
        this.id = id;
        this.fullname = fullname;
        this.jname = jname;
        this.img = img;
    }


    public TeacherDTO(int i, String teacherName) {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Teacher.gender getGender() {
        return gender;
    }

    public void setGender(Teacher.gender gender) {
        this.gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}