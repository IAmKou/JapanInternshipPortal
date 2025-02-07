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

    @Query("SELECT DISTINCT mre FROM MarkReportExam mre  " +
            "JOIN mre.exam e " +
            "JOIN mre.markReport mr Where mr.id  = :markRpId")
    List<MarkReportExam> findAllByMarkRpId(int markRpId);

    @Query("SELECT DISTINCT mre FROM MarkReportExam mre  " +
            "JOIN mre.exam e " +
            "JOIN mre.markReport mr Where mr.id  = :markRpId and mre.exam.title = :examName")
    MarkReportExam findByMarkRpIdAndExamName(int markRpId, String examName);

    @Query("SELECT DISTINCT mre FROM MarkReportExam mre  " +
            "JOIN mre.markReport mr " +
            "JOIN Listt l on l.student.id = mr.student.id " +
            "WHERE l.clas.id = :classId")
    List<MarkReportExam> findAllByClassId(int classId);
}
