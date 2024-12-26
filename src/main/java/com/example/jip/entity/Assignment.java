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
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date created_date;
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date end_date;
    @Column(name = "description")
    String description;
    @Column(name = "content", columnDefinition = "LONGTEXT")
    String content;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assignment_class",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    Set<Class> classes = new HashSet<>();

    @Column(name = "img")
    String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status;


    // Enum for assignment status
    public enum Status {
        OPEN, CLOSE
    }
}
