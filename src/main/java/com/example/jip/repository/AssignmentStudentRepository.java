package com.example.jip.repository;

import com.example.jip.entity.AssignmentStudent;
import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AssignmentStudentRepository extends JpaRepository<AssignmentStudent, Student> {

    int countByStudentId(int studentId);

    List<AssignmentStudent> findByAssignmentId(int assignmentId);
}
