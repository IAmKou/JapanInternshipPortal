package com.example.jip.dto;

import com.example.jip.entity.Account;

public class AccountDTO {
    private Integer id;
    private String username;
    private String roleName;
    private Object profile;  // Can be StudentDTO, TeacherDTO, or ManagerDTO

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.roleName = account.getRole().getName();

        if (account.getStudent() != null) {
            this.profile = new StudentDTO(account.getStudent());
        } else if (account.getTeacher() != null) {
            this.profile = new TeacherDTO(account.getTeacher());
        } else if (account.getManager() != null) {
            this.profile = new ManagerDTO(account.getManager());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Object getProfile() {
        return profile;
    }

    public void setProfile(Object profile) {
        this.profile = profile;
    }
}
