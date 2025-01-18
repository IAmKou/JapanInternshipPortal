package com.example.jip.repository;

import com.example.jip.dto.SemesterDTO;
import com.example.jip.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    boolean existsByName(String name);
    Optional<Semester> findById(int id);
    @Query("SELECT s FROM Semester s WHERE s.start_time <= CURRENT_DATE OR s.end_time < CURRENT_DATE")
    List<Semester> findRelevantSemesters();
    @Query("SELECT s FROM Semester s WHERE s.start_time = :startDate")
    List<Semester> findByStartDate(Date startDate);
    @Query("SELECT s FROM Semester s WHERE s.status = :status")
    Semester findByStatus(@Param("status") Semester.status status);
    // Find semesters ending on a specific date
    @Query("SELECT s FROM Semester s WHERE s.end_time = :endDate")
    List<Semester> findByEndDate(Date endDate);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Semester s WHERE s.end_time = :endTime")
    boolean existsByEndTime(@Param("endTime") Date endTime);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Semester s WHERE s.start_time = :startTime")
    boolean existsByStartTime(@Param("startTime") Date startTime);
    @Query("SELECT s FROM Semester s WHERE s.end_time > :creationDate")
    List<Semester> findSemestersEndingAfter(@Param("creationDate") Date creationDate);

    @Query("SELECT s FROM Semester s join Class c on s.id = c.semester.id WHERE c.id = :classId")
    Semester findSemesterByClassId(int classId);
    @Query("SELECT s FROM Semester s join Class c on s.id = c.semester.id " +
            "                        join Listt l on c.id = l.clas.id" +
            "                        join MarkReport mr on mr.student.id = l.student.id WHERE mr.id = :markRpId")
    Semester findSemesterByMarkRp(int markRpId);
}
