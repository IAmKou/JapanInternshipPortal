package com.example.jip.repository;

import com.example.jip.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Optional<Schedule> findByClassIdAndDate(int classId, Date date);


}
