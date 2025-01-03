package com.example.jip.repository;

import com.example.jip.entity.MarkReportExam;
import com.example.jip.entity.MarkReportExamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkRpExamRepository extends JpaRepository<MarkReportExam, MarkReportExamId> {
    @Query("SELECT DISTINCT mre FROM MarkReportExam mre  " +
            "JOIN mre.exam e " +
            "JOIN mre.markReport mr Where mr.student.id  = :studentId")
    List<MarkReportExam> findAllByStudentId(int studentId);
}
