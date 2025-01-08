package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "Gender", nullable = false)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "img")
    private String img;

    @Column(name="mark")
    private boolean mark;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<StudentAssignment> studentAssignments = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Listt> classLists = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    public enum Gender {
        Male, Female
    }

    // Custom constructor
    public Student(String fullname, String japanname, Date dob, Gender gender, String phoneNumber,
                   String email, String img, Account account) {
        this.fullname = fullname;
        this.japanname = japanname;
        this.dob = dob;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.img = img;
        this.account = account;
    }
}

