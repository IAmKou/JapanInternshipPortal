package com.example.jip.repository;

import com.example.jip.entity.AssignmentStudent;
import com.example.jip.entity.AssignmentStudentId;
import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentStudentRepository extends JpaRepository<AssignmentStudent, Student> {
}
