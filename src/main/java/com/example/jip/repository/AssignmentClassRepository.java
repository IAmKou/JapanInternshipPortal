package com.example.jip.repository;

import com.example.jip.entity.AssignmentClass;
import com.example.jip.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AssignmentClassRepository extends JpaRepository<AssignmentClass, Class> {
    @Modifying
    @Transactional
    @Query("DELETE FROM AssignmentClass ac WHERE ac.assignment.id = :assignmentId")
    void deleteByAssignmentId(@Param("assignmentId") int assignmentId);
}
