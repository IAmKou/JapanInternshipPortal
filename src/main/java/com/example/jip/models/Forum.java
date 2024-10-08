package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Forum {
    private int id;
    private String topic_name;
    private Date date_created;
    private String description;
    private int creator_id;

    public Forum() {}

    public Forum(int id, String topic_name, Date date_created, String description, int creator_id) {
        this.id = id;
        this.topic_name = topic_name;
        this.date_created = date_created;
        this.description = description;
        this.creator_id = creator_id;
    }
}
