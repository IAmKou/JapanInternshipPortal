package com.example.jip.repository;

import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate ( @Param("classId")int classId,
                                          @Param("date")Date date);
    Optional<Schedule> findByDateAndSemester(java.sql.Date date, Semester semester);  // Check if schedule for a holiday already exists


    List<Schedule> findBySemesterId(int semesterId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.semester.id = :semesterId")
    void deleteBySemesterId(@Param("semesterId") int semesterId);


}
