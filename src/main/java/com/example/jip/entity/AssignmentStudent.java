package com.example.jip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assignment_student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStudent {

    @EmbeddedId
    private AssignmentStudentId id;

    @ManyToOne
    @MapsId("assignment_id")
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @MapsId("student_id")
    @JoinColumn(name = "student_id")
    private Student student;

    public AssignmentStudent(Assignment assignment, Student student) {
        this.id = new AssignmentStudentId(assignment.getId(), student.getId());
        this.assignment = assignment;
        this.student = student;
    }
}