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
    private String fullname;
    private String email;
    private String phonenumber;
    private gender Gender;
    private String img;
    private int account_id;

    public Manager() {}

    public Manager(int id, String fullname, String email, String phonenumber, gender gender, String img, int account_id) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.phonenumber = phonenumber;
        Gender = gender;
        this.img = img;
        this.account_id = account_id;
    }

    public enum gender {
        MALE, FEMALE
    }

}
