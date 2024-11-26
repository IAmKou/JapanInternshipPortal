package com.example.jip.repository;

import com.example.jip.entity.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AttendantRepository extends JpaRepository<Attendant, Integer> {
    List<Attendant> findByScheduleId(int scheduleId);
    @Query("SELECT a FROM Attendant a " +
            "WHERE a.schedule.id = :scheduleId AND a.date = :date " +
            "AND a.schedule.start_time <= :currentTime AND a.schedule.end_time >= :currentTime")
    List<Attendant> findByScheduleIdAndTimeSlot(
            @Param("scheduleId") int scheduleId,
            @Param("date") Date date,
            @Param("currentTime") Time currentTime);

}
