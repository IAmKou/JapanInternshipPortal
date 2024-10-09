package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Application {
    private int id;
    private String img;
    private String category;
    private String description;
    private String content;
    private Date date;
    private int student_id;
    private status status;
    private String reply;
    private Date reply_date;

    public enum status{
        Pending, Approved, Rejected
    }

    public Application() {}

    public Application(int id, String img, String category, String description, String content, Date date, int student_id, Application.status status, String reply, Date reply_date) {
        this.id = id;
        this.img = img;
        this.category = category;
        this.description = description;
        this.content = content;
        this.date = date;
        this.student_id = student_id;
        this.status = status;
        this.reply = reply;
        this.reply_date = reply_date;
    }
}
