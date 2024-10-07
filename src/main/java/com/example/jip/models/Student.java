package com.example.jip.models;


import java.sql.Date;

public class Student {
    private int id;
    private String Fullname;
    private Date DoB;
    private String Gender;
    private String PhoneNumber;
    private String img;
    private String email;
    private int account_id;

    public Student() {}

    public Student(int id, String fullname, Date doB, String gender, String phoneNumber, String img, String email, int account_id) {
        this.id = id;
        Fullname = fullname;
        DoB = doB;
        Gender = gender;
        PhoneNumber = phoneNumber;
        this.img = img;
        this.email = email;
        this.account_id = account_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public Date getDoB() {
        return DoB;
    }

    public void setDoB(Date doB) {
        DoB = doB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}
