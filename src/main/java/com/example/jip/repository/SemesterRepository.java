package com.example.jip.repository;

import com.example.jip.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.sql.Date;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    boolean existsByName(String name);
    Optional<Semester> findById(int id);
    @Query("SELECT s FROM Semester s WHERE s.start_time = CURRENT_DATE OR s.end_time = CURRENT_DATE")
    List<Semester> findSemestersByCurrentDate();
    @Query("SELECT s FROM Semester s WHERE s.start_time = :startDate")
    List<Semester> findByStartDate(Date startDate);

    // Find semesters ending on a specific date
    @Query("SELECT s FROM Semester s WHERE s.end_time = :endDate")
    List<Semester> findByEndDate(Date endDate);

}
