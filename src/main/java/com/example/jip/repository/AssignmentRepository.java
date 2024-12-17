package com.example.jip.repository;

import com.example.jip.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    @Query("SELECT DISTINCT a FROM Assignment a " +
            "JOIN a.classes c " +
            "JOIN c.classLists l " +
            "WHERE l.student.id = :studentId")
    List<Assignment> findAssignmentsByStudentId(@Param("studentId") int studentId);

    List<Assignment> findAssignmentsByTeacherId(int teacherId);

    boolean existsByDescription(String description);
}
