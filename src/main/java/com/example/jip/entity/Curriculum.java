package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
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

    @Column(name="total_time")
    private int total_time;


    public Curriculum(String subject, int total_slot, int total_time) {
        this.subject = subject;
        this.total_slot = total_slot;
        this.total_time = total_time;
    }


    public Curriculum() {
    }
    public String getDescription(CurriculumInformation curriculumInformation) {
        return curriculumInformation != null ? curriculumInformation.getDescription() : null;
    }
}
