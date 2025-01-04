package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assignment_class")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentClass {

    @EmbeddedId
    private AssignmentClassId id;

    @ManyToOne
    @MapsId("assignment_id")
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @MapsId("class_id")
    @JoinColumn(name = "class_id")
    private Class clas;

    public AssignmentClass(Assignment assignment, Class clas) {
        this.id = new AssignmentClassId(assignment.getId(), clas.getId());
        this.assignment = assignment;
        this.clas = clas;
    }


}
