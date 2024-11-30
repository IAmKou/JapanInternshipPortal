package com.example.jip.repository;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.dto.StudentScheduleDTO;
import com.example.jip.entity.Schedule;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate ( @Param("classId")int classId,
                                          @Param("date")Date date);

    @Query(value = "SELECT " +
            "s.id AS schedule_id, " +
            "c.name AS class_name, " +
            "t.fullname AS teacher_name, " +
            "s.day_of_week, " +
            "s.start_time, " +
            "s.end_time, " +
            "COALESCE(a.status, 'No Record') AS attendance_status, " +
            "s.date, " +
            "s.description, " +
            "s.event " +
            "FROM Schedule s " +
            "JOIN Class c ON s.class_id = c.id " +
            "JOIN Teacher t ON c.teacher_id = t.id " +
            "JOIN List l ON l.class_id = s.class_id " +
            "LEFT JOIN Attendant a ON s.id = a.schedule_id AND a.student_id = :studentId " +
            "WHERE l.student_id = :studentId " +
            "ORDER BY s.date, s.start_time", nativeQuery = true)
    List<Object[]> findStudentSchedule(@Param("studentId") int studentId);


    @Query("SELECT new com.example.jip.dto.ScheduleDTO( "
            + "s.day_of_week, c.id, s.start_time, s.end_time, s.description, s.event, "
            + "s.date, s.location, c.name, c.teacher.id, c.teacher.fullname) "
            + "FROM Schedule s "
            + "JOIN s.clasz c "
            + "WHERE c.teacher.id = :teacherId")
    List<ScheduleDTO> findTeacherSchedule(@Param("teacherId") int teacherId);

    @Query("SELECT new com.example.jip.dto.ScheduleDTO( "
            + "s.day_of_week, c.id, s.start_time, s.end_time, s.description, s.event, "
            + "s.date, s.location, c.name, c.teacher.id, c.teacher.fullname) "
            + "FROM Schedule s "
            + "JOIN s.clasz c "
            + "WHERE c.name LIKE %:className%")
    List<ScheduleDTO> findByClassName(@Param("className") String className);

    List<Schedule> findByDate(Date date);

    @Query("SELECT new com.example.jip.dto.ScheduleDTO( "
            + "s.day_of_week, c.id, s.start_time, s.end_time, s.description, s.event, "
            + "s.date, s.location, c.name, c.teacher.id, c.teacher.fullname) "
            + "FROM Schedule s "
            + "JOIN s.clasz c "
            + "WHERE c.teacher.fullname LIKE %:teacherName% ")
    List<ScheduleDTO> findByTeacherName(@Param("teacherName") String teacherName);


    @Query("SELECT new com.example.jip.dto.ScheduleDTO(s.day_of_week, c.id, s.start_time, s.end_time, s.description, s.event, s.date, s.location, c.name, c.teacher.id, c.teacher.fullname) " +
            "FROM Schedule s " +
            "JOIN s.clasz c " +
            "WHERE c.name LIKE %:className% AND c.teacher.fullname LIKE %:teacherName% AND s.date = :date")
    List<ScheduleDTO> findByClassNameAndTeacherNameAndDate(@Param("className") String className,
                                                           @Param("teacherName") String teacherName,
                                                           @Param("date") Date date);
}
