package com.example.jip.dto;

import java.sql.Date;

public class ThreadDTO {

    private int id;
    private String topicName;
    private Date dateCreated;
    private String description;
    private int creatorId;
    private String creatorName;
    private byte[] image;

    // Default Constructor
    public ThreadDTO() {}

    // Constructor with parameters
    public ThreadDTO(int id, String topicName, Date dateCreated, String description, int creatorId, byte[] image, String creatorName) {
        this.id = id;
        this.topicName = topicName;
        this.dateCreated = dateCreated;
        this.description = description;
        this.creatorId = creatorId;
        this.image = image;
        this.creatorName = creatorName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}