package com.example.jip.repository;

import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import com.example.jip.entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Repository
public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Integer> {
    boolean existsByStudentIdAndAssignmentId(int studentId, int assignmentId);

    @Query("SELECT sa.assignment.id FROM StudentAssignment sa WHERE sa.student.id = :studentId")
    List<Integer> findSubmittedAssignmentIdsByStudentId(@Param("studentId") int studentId);

    List<StudentAssignment> findAllByStudentId(int studentId);

    @Query("SELECT sa FROM StudentAssignment sa WHERE sa.assignment.id = :assignmentId AND sa.status = :status")
    List<StudentAssignment> findByAssignmentIdAndStatus(@Param("assignmentId") int assignmentId, @Param("status") StudentAssignment.Status status);

    List<StudentAssignment> findByAssignmentId(int assignmentId);

    int countStudentAssignmentByStudentId(int studentId);
}
