package com.example.jip.repository;

import com.example.jip.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate(int classId, Date date);


}
