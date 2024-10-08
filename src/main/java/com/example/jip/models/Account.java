package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {
    private int id;
    private String Username;
    private String Password;
    private int role_id;

    public Account() {}

    public Account(int id, String username, String password, int role_id) {
        this.id = id;
        Username = username;
        Password = password;
        this.role_id = role_id;
    }

}
