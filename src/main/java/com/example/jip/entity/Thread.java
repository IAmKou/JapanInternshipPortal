package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
@Entity
@Getter
@Setter
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String topicName;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
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
