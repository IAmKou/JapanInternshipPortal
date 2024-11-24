package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Entity
@Getter
@Setter
public class Thread {
    @Id
    @Temporal(TemporalType.DATE)
    private int id;
    private String topicName;
    private Date dateCreated;
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String description;
    private int creatorId;

    @Lob
    @Column(columnDefinition="LONGBLOB")
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
