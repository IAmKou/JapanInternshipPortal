package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

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

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;


    @JsonManagedReference
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Class> classes;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Schedule> schedules;


    public Teacher(){}

    public Teacher(int id, String fullname, String jname, String email, String phoneNumber, gender gender, String img, Account account) {
        this.id = id;
        Fullname = fullname;
        Jname = jname;
        this.email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
        this.img = img;
        this.account = account;
    }

    public enum gender{
        Male,Female
    }

}
