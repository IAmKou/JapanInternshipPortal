package com.example.jip.repository;

import com.example.jip.dto.ClassScheduleDTO;
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
    boolean existsByClaszIdAndSemesterId(int classId, int semesterId);

    boolean existsByRoomAndDateAndSemesterId(String room, java.sql.Date date, int semesterId);

    List<Schedule> findBySemesterIdAndStatus(int semesterId, Schedule.status status);

    boolean existsBySemesterIdAndDateAndActivityContaining(int semesterId, java.sql.Date date, String activity);

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.semester.id = :semesterId")
    void deleteBySemesterId(@Param("semesterId") int semesterId);

    @Query("SELECT s FROM Schedule s WHERE s.semester.id = :semesterId AND s.date = :date AND s.activity = :activity")
    Schedule findBySemesterIdAndDateAndActivity(@Param("semesterId") int semesterId,
                                                @Param("date") java.sql.Date date,
                                                @Param("activity") String activity);

    @Query("SELECT new com.example.jip.dto.ClassScheduleDTO(c.id, c.name, s.room, sem.name) " +
            "FROM Schedule s " +
            "JOIN s.clasz c " +
            "JOIN s.semester sem " +
            "GROUP BY c.id, c.name, s.room, sem.name")
    List<ClassScheduleDTO> findUniqueClassSchedule();
}
