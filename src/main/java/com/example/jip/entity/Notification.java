package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Notification")
@Getter
@Setter

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="Title", nullable=false)
    private String title;

    @Column(name="content", nullable=false)
    private String content;

    @ManyToOne()
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "Id")
    private Account account;

    public Notification() {}

    public Notification(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Notification(int id, String title, String content, Account account) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.account = account;
    }
}
