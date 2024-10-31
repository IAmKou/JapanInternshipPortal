package com.example.jip.dto;

import com.example.jip.entity.Manager;

public class ManagerDTO {
    private String fullname;
    private String jname;
    private String email;
    private String phoneNumber;
    private Manager.Gender gender;
    private String img;


    public ManagerDTO(Manager manager) {
        this.fullname = manager.getFullname();
        this.jname = manager.getJname();
        this.email = manager.getEmail();
        this.phoneNumber = manager.getPhoneNumber();
        this.gender = manager.getGender();
        this.img = manager.getImg();
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

    public Manager.Gender getGender() {
        return gender;
    }

    public void setGender(Manager.Gender gender) {
        this.gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
