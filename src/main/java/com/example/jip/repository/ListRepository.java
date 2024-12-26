package com.example.jip.repository;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.StudentWithClassDTO;
import com.example.jip.entity.ListId;
import com.example.jip.entity.Listt;
import com.example.jip.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListRepository extends JpaRepository<Listt, ListId> {
    @Query("SELECT l.student FROM Listt l WHERE l.clas.id = :classId")
    List<Student> findStudentsByClassId(int classId);


    @Modifying
    @Transactional
    @Query("Delete from Listt where clas.id = :classId")
    void deleteStudentsByClassId(int classId);

    @Query("SELECT new com.example.jip.dto.StudentWithClassDTO(s.id, s.fullname, s.gender, s.img, c.name) " +
            "FROM Student s " +
            "LEFT JOIN Listt l ON s.id = l.student.id " +
            "LEFT JOIN Class c ON l.clas.id = c.id " +
            "WHERE s.id NOT IN (SELECT l.student.id FROM Listt l)")
    List<StudentWithClassDTO> getStudentsWithoutClass();

    @Query("SELECT new com.example.jip.dto.StudentWithClassDTO(s.id, s.fullname, s.gender, " +
            " s.img, c.name) " +
            "FROM Student s " +
            "LEFT JOIN Listt l ON s.id = l.student.id " +
            "LEFT JOIN Class c ON l.clas.id = c.id")
    List<StudentWithClassDTO> findAllStudentsWithClassInfo();

    @Query("SELECT new com.example.jip.dto.MarkReportDTO(s.id, s.fullname, mr.softskill, mr.avg_exam_mark, " +
            "mr.middle_exam, mr.final_exam, mr.attitude, mr.final_mark, mr.comment) " +
            "FROM Student s " +
            "JOIN MarkReport mr ON s.id = mr.student.id " +
            "JOIN Listt l ON s.id = l.student.id " +
            "WHERE l.clas.id = :classId")
    List<MarkReportDTO> getStudentsWithMarkReportsByClassId(@Param("classId") Integer classId);
}
