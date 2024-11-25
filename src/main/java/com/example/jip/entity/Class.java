package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "number_of_student", nullable = false)
    int number_of_student;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @ManyToMany(mappedBy = "classes")
    Set<Assignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "clas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Listt> studentLists = new HashSet<>();
}
