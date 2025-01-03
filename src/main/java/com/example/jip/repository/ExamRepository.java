package com.example.jip.repository;

import com.example.jip.dto.response.exam.ExamResponse;
import com.example.jip.entity.Exam;
import com.example.jip.entity.MarkReportExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

}
