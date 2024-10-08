package com.example.jip.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Message {
    private int id;
    private int sender_id;
    private int recepient_id;
    private String content;
    private Date sent_time;

    public Message() {}

    public Message(int id, int sender_id, int recepient_id, String content, Date sent_time) {
        this.id = id;
        this.sender_id = sender_id;
        this.recepient_id = recepient_id;
        this.content = content;
        this.sent_time = sent_time;
    }
}
