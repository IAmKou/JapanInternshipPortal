package com.example.jip.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Entity
@Table(name="Student")
@Setter
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String Fullname;
    private Date DoB;
    private gender Gender;
    private String PhoneNumber;
    private String img;
    private String email;
    private int account_id;

    public Student() {}

    public Student(int id, String fullname, Date doB, gender gender, String phoneNumber, String img, String email, int account_id) {
        this.id = id;
        Fullname = fullname;
        DoB = doB;
        Gender = gender;
        PhoneNumber = phoneNumber;
        this.img = img;
        this.email = email;
        this.account_id = account_id;
    }

    public enum gender{
        Male,Female
    }

}
