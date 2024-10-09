package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Comment {
    private int id;
    private String content;
    private Date date;
    private int creator_id;

    public Comment(){}

    public Comment(int id, String content, Date date, int creator_id) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.creator_id = creator_id;
    }
}
