package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="Teacher")
@Setter
@Getter
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="Fullname", nullable = false)
    private String fullname;

    @Column(name="Jname", nullable = false)
    private String jname;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="Gender", nullable = false)
    private gender gender;

    @Column(name="img")
    private String img;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;


    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Class> classes;

    public Teacher(){}

    public Teacher(int id, String fullname, String jname, String email, String phoneNumber, Teacher.gender gender, String img, Account account, List<Class> classes) {
        this.id = id;
        this.fullname = fullname;
        this.jname = jname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.img = img;
        this.account = account;
        this.classes = classes;
    }

    public enum gender{
        Male,Female
    }

}
