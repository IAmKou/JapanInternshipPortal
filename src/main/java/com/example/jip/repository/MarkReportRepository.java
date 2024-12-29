package com.example.jip.repository;

import com.example.jip.entity.MarkReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkReportRepository extends CrudRepository<MarkReport, Integer> {

    @Query ("SELECT DISTINCT mr " +
            "FROM MarkReport mr JOIN Listt l " +
            "on mr.student.id = l.student.id " +
            "AND l.clas.id = :classId")
    List<MarkReport> findAllByClassId(int classId);

    @Query ("SELECT DISTINCT mr " +
            "FROM MarkReport mr " +
            "WHERE mr.student.email = :email")
    MarkReport findByEmail(String email);

    MarkReport findById(int id);
    MarkReport findByStudentId(int studentId);
    List<MarkReport> findByStudentIdIn(List<Integer> studentIds);
}
