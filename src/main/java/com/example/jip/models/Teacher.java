package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Teacher {
    private int id;
    private String Fullname;
    private String email;
    private String PhoneNumber;
    private gender Gender;
    private String img;
    private int account_id;

    public Teacher(){}

    public Teacher(int id, String fullname, String email, String phoneNumber, gender gender, String img, int account_id) {
        this.id = id;
        Fullname = fullname;
        this.email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
        this.img = img;
        this.account_id = account_id;
    }

    public enum gender{
        Male,Female
    }

}
