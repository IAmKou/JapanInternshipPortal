package com.example.jip.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Post {
    private int id;
    private String replies;
    private Date date_created;
    private int creator_id;
    private byte[] image;

    public Post() {}

    public Post(int id, String replies, Date date_created, int creator_id, byte[] image) {
        this.id = id;
        this.replies = replies;
        this.date_created = date_created;
        this.creator_id = creator_id;
        this.image = image;
    }
}
