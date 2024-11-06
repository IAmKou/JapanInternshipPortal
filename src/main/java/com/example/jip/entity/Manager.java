package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private String PhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="Gender", nullable = false)
    private Gender Gender;

    @Column(name="img")
    private String img;

    @Column(name="account_id", insertable = false, updatable = false)
    private int account_id;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    public Manager() {}

    public Manager(int id, String fullname, String jname, String email, String phonenumber, Gender gender, String img, int account_id) {
        this.id = id;
        Fullname = fullname;
        Jname = jname;
        this.email = email;
        PhoneNumber = phonenumber;
        Gender = gender;
        this.img = img;
        this.account_id = account_id;
    }

    public enum Gender {
        MALE, FEMALE
    }
}
