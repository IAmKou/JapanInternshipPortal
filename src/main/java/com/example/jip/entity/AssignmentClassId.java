package com.example.jip.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AssignmentClassId implements Serializable {
    private int assignment_id;
    private int class_id;

    // Constructors, getters, setters, equals, and hashCode


}