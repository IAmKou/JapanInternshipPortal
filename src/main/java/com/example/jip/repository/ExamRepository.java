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
    @Query("SELECT new com.example.jip.dto.response.exam.ExamResponse(mr.id, e.id, e.title, e.kanji, e.kotoba, e.bunpou) FROM Exam e " +
            "JOIN MarkReportExam mre On e.id = mre.exam.id " +
            "JOIN MarkReport mr on mr.id = mre.markReport.id  Where mr.student.id  = :studentId")
    List<ExamResponse> findAllByStudentId(int studentId);
}
