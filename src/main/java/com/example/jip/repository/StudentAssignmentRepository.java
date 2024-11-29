package com.example.jip.repository;

import com.example.jip.entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Integer> {
    boolean existsByStudentIdAndAssignmentId(int studentId, int assignmentId);

    @Query("SELECT sa.assignment.id FROM StudentAssignment sa WHERE sa.student.id = :studentId")
    List<Integer> findSubmittedAssignmentIdsByStudentId(@Param("studentId") int studentId);

    List<StudentAssignment> findByStudentId(int studentId);

    List<StudentAssignment> findByAssignmentId(int assignmentId);

}
