package com.example.jip.models;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }
}
