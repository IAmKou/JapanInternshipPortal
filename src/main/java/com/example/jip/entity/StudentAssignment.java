package com.example.jip.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
@Entity
@Table(name = "student_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    Assignment assignment;

    @Column(name = "mark")
    BigDecimal mark;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "date", nullable = false)
    Date date;

    @Enumerated(EnumType.STRING) // Use String for readability
    @Column(name = "status", nullable = false)
    Status status; // NEW: Status field

    public enum Status {
        SUBMITTED, // Editable/Deletable
        MARKED     // Not editable/deletable
    }

}
