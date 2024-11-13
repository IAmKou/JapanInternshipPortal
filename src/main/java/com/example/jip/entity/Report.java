package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="report")
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="Title", nullable=false)
    private String title;

    @Column(name="Content", nullable=false)
    private String content;

    @ManyToOne()
    @JoinColumn(name = "reporter_id", nullable = false, referencedColumnName = "Id")
    private Account account;

    public Report(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Report() {}
}
