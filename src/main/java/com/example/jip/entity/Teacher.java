package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="Teacher")
@Setter
@Getter
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String Fullname;
    private String Jname;
    private String email;
    private String PhoneNumber;
    private gender Gender;
    private String img;
    private int account_id;

    public Teacher(){}

    public Teacher(int id, String fullname, String jname, String email, String phoneNumber, gender gender, String img, int account_id) {
        this.id = id;
        Fullname = fullname;
        Jname = jname;
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
