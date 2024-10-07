package com.example.model;

public class Teacher {
    private int id;
    private String Fullname;
    private String email;
    private String PhoneNumber;
    private String Gender;
    private String img;
    private int account_id;

    public Teacher(){}

    public Teacher(int id, String fullname, String email, String phoneNumber, String gender, String img, int account_id) {
        this.id = id;
        Fullname = fullname;
        this.email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
        this.img = img;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}
