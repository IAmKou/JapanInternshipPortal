package com.example.jip.dto;

import com.example.jip.entity.Account;
import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;

import java.sql.Date;

public class AccountDTO extends Account {
    private Integer id;
    private String username;
    private String roleName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date dob;
    private String jname;
    private String gender;
    private int studentId;
    private int teacherId;
    private String img;
    private boolean mark;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.roleName = (account.getRole() != null) ? account.getRole().getName() : "UNKNOWN";

        if (account.getStudent() != null) {
            Student student = account.getStudent();
            this.studentId = student.getId();
            this.fullName = student.getFullname();
            this.email = student.getEmail();
            this.phoneNumber = student.getPhoneNumber();
            this.dob = student.getDob();
            this.jname = student.getJapanname();
            this.gender = (student.getGender() != null) ? student.getGender().toString() : "UNKNOWN";
            this.mark = student.isMark();
            this.img = student.getImg();
        } else if (account.getTeacher() != null) {
            Teacher teacher = account.getTeacher();
            this.teacherId = teacher.getId();
            this.fullName = teacher.getFullname();
            this.email = teacher.getEmail();
            this.phoneNumber = teacher.getPhoneNumber();
            this.jname = teacher.getJname();
            this.gender = (teacher.getGender() != null) ? teacher.getGender().toString() : "UNKNOWN";
            this.img = teacher.getImg();
        } else if (account.getManager() != null) {
            Manager manager = account.getManager();
            this.fullName = manager.getFullname();
            this.email = manager.getEmail();
            this.phoneNumber = manager.getPhoneNumber();
            this.jname = manager.getJname();
            this.gender = (manager.getGender() != null) ? manager.getGender().toString() : "UNKNOWN";
            this.img = manager.getImg();
        } else {
            System.out.println("No profile found for account ID: " + account.getId());
        }
    }

    public AccountDTO() {}


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
}
