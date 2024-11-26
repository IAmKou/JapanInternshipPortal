package com.example.jip.repository;

import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    @Query("SELECT a FROM Assignment a " +
            "JOIN a.classes c " +
            "JOIN Listt l ON l.clas.id = c.id " +
            "WHERE l.student.id = :studentId")
    List<Assignment> findAssignmentsByStudentId(@Param("studentId") int studentId);
}
