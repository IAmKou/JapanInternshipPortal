package com.example.jip.dto;
import com.example.jip.entity.Student;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class StudentDTO {
    private int id;
    private String fullname;
    private String jname;
    private Student.Gender gender;
    private String img;
    private boolean mark;
    private String email;
    private String phone;
    private Date dob;

    public StudentDTO(Student student) {
        this.id = student.getId();
        this.fullname = student.getFullname();
        this.jname = student.getJapanname();
        this.gender = student.getGender();
        this.img = student.getImg();
        this.mark = student.isMark();
        this.email = student.getEmail();
        this.phone = student.getPhoneNumber();
        this.dob = student.getDob();
    }

    public StudentDTO() {}

    public StudentDTO(int id, String fullname,Student.Gender gender, String img, boolean mark) {
        this.id = id;
        this.fullname = fullname;
        this.gender = gender;
        this.img = img;
        this.mark = mark;
    }
}
