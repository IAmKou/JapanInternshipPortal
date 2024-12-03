package com.example.jip.repository;

import com.example.jip.entity.Exam;
import com.example.jip.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Integer> {

    List<ExamResult> findAllByExamId(int examId);

    List<ExamResult> findAllByStudentId(int studentId);
}
