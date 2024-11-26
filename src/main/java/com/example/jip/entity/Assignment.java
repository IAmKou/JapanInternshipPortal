package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Entity
@Table(name = "assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name =  "date_created")
    @Temporal(TemporalType.DATE)
    Date created_date;
    @Column(name =  "end_date")
    @Temporal(TemporalType.DATE)
    Date end_date;
    @Column(name = "description")
    String description;
    @Column(name = "content")
    String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "assignment_class",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    Set<Class> classes = new HashSet<>();

    @Column(name = "img")
    String imgUrl;

}
