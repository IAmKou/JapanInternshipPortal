package com.example.jip.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
public class Thread {
    @Temporal(TemporalType.DATE)
    private int id;
    private String topicName;
    private Date dateCreated;
    private String description;
    private int creatorId;
    private byte[] image;

    public Thread() {}

    public Thread(int id, String topicName, Date dateCreated, String description, int creatorId, byte[] image) {
        this.id = id;
        this.topicName = topicName;
        this.dateCreated = dateCreated;
        this.description = description;
        this.creatorId = creatorId;
        this.image = image;
    }
}
