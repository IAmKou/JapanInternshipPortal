package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="Manager")
@Getter
@Setter
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="Fullname", nullable = false)
    private String Fullname;

    @Column(name="Jname", nullable = false)
    private String Jname;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="Gender", nullable = false)
    private Gender Gender;

    @Column(name="img")
    private String img;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;


    public Manager() {}

    public Manager(int id, String fullname, String jname, String email, String phoneNumber, Manager.Gender gender, String img, Account account) {
        this.id = id;
        Fullname = fullname;
        Jname = jname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        Gender = gender;
        this.img = img;
        this.account = account;
    }

    public enum Gender {
        Male, Female
    }
}
