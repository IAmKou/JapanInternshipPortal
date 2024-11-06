package com.example.jip.repository;

import com.example.jip.entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Integer> {
}
