package com.example.jip.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStudentId implements Serializable {
    private int assignment_id;
    private int student_id;

    // Constructors, getters, setters, equals, and hashCode methods
}
