package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "Student")
@Setter
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Fullname", nullable = false)
    private String fullname;

    @Column(name = "Japanname", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String japanname;

    @Column(name = "DoB", nullable = false)
    private Date dob;

    @Column(name = "Passport_url")
    private String passport;

    @Enumerated(EnumType.STRING)
    @Column(name = "Gender", nullable = false)
    private Gender gender;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "img")
    private String img;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "account_id")
    private int accountId;

    public Student() {}

    public Student(int id, String fullname, String japanname, Date dob, String passport, Gender gender, String phoneNumber, String img, String email, int accountId) {
        this.id = id;
        this.fullname = fullname;
        this.japanname = japanname;
        this.dob = dob;
        this.passport = passport;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.img = img;
        this.email = email;
        this.accountId = accountId;
    }

    public enum Gender {
        Male, Female
    }
}
