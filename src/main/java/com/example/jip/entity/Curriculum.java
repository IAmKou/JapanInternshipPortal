package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="curriculum")
@Getter
@Setter
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="subject")
    private String subject;

    @Column(name="total_slot")
    private int total_slot;

    @Column(name="subject_description")
    private String description;

    @Column(name="total_time")
    private int total_time;


    public Curriculum(int id, String subject, int total_slot, String description, int total_time) {
        this.id = id;
        this.subject = subject;
        this.total_slot = total_slot;
        this.description = description;
        this.total_time = total_time;
    }

    public Curriculum() {
    }
}
