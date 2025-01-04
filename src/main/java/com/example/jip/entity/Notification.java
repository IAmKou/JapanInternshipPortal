package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "Id")
    private Account account; // Sender's account

    @ManyToOne
    @JoinColumn(name = "recipient_account_id", referencedColumnName = "Id")
    private Account recipientAccount; // Optional recipient account

    public Notification() {}

    public Notification(int id, String title, Date createdAt, Account account, Account recipientAccount) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.account = account;
        this.recipientAccount = recipientAccount;

    }

    public Notification(String title, Date currentDateTime, Integer id, Integer id1) {
        this.title = title;
        this.createdAt = currentDateTime;
        this.id = id;
        this.recipientAccount = null;
    }
}

