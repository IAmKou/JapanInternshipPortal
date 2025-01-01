package com.example.jip.repository;

import com.example.jip.entity.MarkReportExam;
import com.example.jip.entity.MarkReportExamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRpExamRepository extends JpaRepository<MarkReportExam, MarkReportExamId> {
}
