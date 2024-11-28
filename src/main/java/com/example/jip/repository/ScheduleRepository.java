package com.example.jip.repository;

import com.example.jip.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate(int classId, Date date);

    @Query("""
        SELECT (s.id, c.name, t.Fullname, s.start_time, s.end_time, s.description)
        FROM Schedule s
        JOIN Class c ON s.clasz.id = c.id
        JOIN Teacher t ON c.teacher.id = t.id
        JOIN Listt l ON l.clas.id = s.clasz.id
        WHERE l.student.id = :studentId
    """)
    List<Schedule> findStudentSchedule(@Param("studentId") int studentId);

    @Query("""
        SELECT (s.id, c.name, s.start_time, s.end_time, s.description)
        FROM Schedule s
        JOIN Class c ON s.clasz.id = c.id
        WHERE c.teacher.id = :teacherId
    """)
    List<Schedule> findTeacherSchedule(@Param("teacherId") int teacherId);


}
