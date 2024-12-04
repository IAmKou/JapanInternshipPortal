package com.example.jip.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class ThreadDTO {
    private int id;
    private String topicName;
    private java.sql.Date dateCreated;
    private String description;
    private int creatorId;
    private String creatorName;
    private byte[] image;

    public ThreadDTO(int id, String topicName, Date dateCreated, String description, int creatorId, byte[] image, String creatorName) {
        this.id = id;
        this.topicName = topicName;
        this.dateCreated = dateCreated;
        this.description = description;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.image = image;
    }

}