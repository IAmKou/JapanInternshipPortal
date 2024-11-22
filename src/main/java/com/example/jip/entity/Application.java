package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "img")
    private String img;

    @Column(name =  "date_created", nullable = false)
    private Date created_date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name =  "reply")
    private String reply;

    @Column(name =  "date_replied")
    private Date replied_date;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = true)
    private Teacher teacher;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    public enum Status { // Đổi từ 'status' thành 'Status'
        Pending, Approved, Rejected
    }

    public Application() {}

    public Application(int id, String name, String category, String content, String img, Date created_date, Status status, String reply, Date replied_date, Teacher teacher, Student student) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.content = content;
        this.img = img;
        this.created_date = created_date;
        this.status = status;
        this.reply = reply;
        this.replied_date = replied_date;
        this.teacher = teacher;
        this.student = student;
    }
}
