package com.example.jip.repository;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.entity.MarkReport;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MarkReportRepository extends CrudRepository<MarkReport, Integer> {

    @Query("SELECT DISTINCT mr " +
            "FROM MarkReport mr JOIN Listt l " +
            "on mr.student.id = l.student.id " +
            "AND l.clas.id = :classId")
    List<MarkReport> findAllByClassId(int classId);

    @Query("SELECT DISTINCT mr " +
            "FROM MarkReport mr " +
            "WHERE mr.student.email = :email")
    MarkReport findByEmail(String email);

    MarkReport findById(int id);

    MarkReport findByStudentId(int studentId);

    @Modifying
    @Transactional
    @Query("UPDATE MarkReport m SET " +
            "m.comment = :comment, " +
            "m.attitude = :attitude, " +
            "m.softskill = :softskill, " +
            "m.skill = :skill, " +
            "m.avg_exam_mark = :avgExamMark, " +
            "m.middle_exam = :middleExam, " +
            "m.final_exam = :finalExam, " +
            "m.final_mark = :finalMark " +
            "WHERE m.student.id = :studentId")
    void updateGrade(
            @Param("studentId") Integer studentId,
            @Param("comment") String comment,
            @Param("attitude") BigDecimal attitude,
            @Param("softskill") BigDecimal softskill,
            @Param("skill") BigDecimal skill,
            @Param("avgExamMark") BigDecimal avgExamMark,
            @Param("middleExam") BigDecimal middleExam,
            @Param("finalExam") BigDecimal finalExam,
            @Param("finalMark") BigDecimal finalMark);
}
