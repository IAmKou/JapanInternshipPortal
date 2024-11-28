package com.example.jip.repository;

import com.example.jip.entity.Exam;
import com.example.jip.entity.ExamResult;
import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Integer> {
    Optional<ExamResult> findByExamAndStudentId(Exam exam, int studentId);
}
