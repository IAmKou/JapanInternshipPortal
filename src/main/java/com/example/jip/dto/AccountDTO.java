package com.example.jip.dto;

import com.example.jip.entity.Account;
import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;

import java.sql.Date;

public class AccountDTO {
    private Integer id;
    private String username;
    private String roleName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date dob;
    private String jname;
    private String gender;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.roleName = account.getRole().getName();

        if (account.getStudent() != null) {
            Student student = account.getStudent();
            this.fullName = student.getFullname();
            this.email = student.getEmail();
            this.phoneNumber = student.getPhoneNumber();
            this.dob = student.getDob();
            this.jname = student.getJapanname();
            this.gender = student.getGender().toString();

        } else if (account.getTeacher() != null) {
            Teacher teacher = account.getTeacher();
            this.fullName = teacher.getFullname();
            this.email = teacher.getEmail();
            this.phoneNumber = teacher.getPhoneNumber();
            this.jname = teacher.getJname();
            this.gender = teacher.getGender().toString();

        } else if (account.getManager() != null) {
            Manager manager = account.getManager();
            this.fullName = manager.getFullname();
            this.email = manager.getEmail();
            this.phoneNumber = manager.getPhoneNumber();
            this.jname = manager.getJname();
            this.gender = manager.getGender().toString();

        } else {
            System.out.println("No profile found for account ID: " + account.getId());
        }
    }

    public AccountDTO() {
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getJname() {
        return jname;
    }

    public void setJname(String jname) {
        this.jname = jname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
