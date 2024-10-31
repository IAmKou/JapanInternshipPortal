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
    private gender Gender;

    @Column(name="img")
    private String img;

    @Column(name="account_id")
    private int account_id;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

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
