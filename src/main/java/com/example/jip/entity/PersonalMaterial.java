package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PersonalMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "material_link")
    private String material_link;

    @ManyToOne(fetch = FetchType.LAZY)  // Liên kết với Material
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;  // Trường liên kết với Material

    public PersonalMaterial() {}

    public PersonalMaterial(int id, Student student, String material_link, Material material) {
        this.id = id;
        this.student = student;
        this.material_link = material_link;
        this.material = material;
    }
}
