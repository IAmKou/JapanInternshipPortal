package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CurriculumInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Foreign Key relationship to the Curriculum table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id") // Trường tham chiếu đến bảng `curriculum`
    private Curriculum curriculum;

    // Column for description
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    public CurriculumInformation() {
    }

    public CurriculumInformation(Curriculum curriculum, String description) {
        this.curriculum = curriculum;
        this.description = description;
    }
}