package com.example.jip.repository;

import com.example.jip.dto.ScheduleDTO;
import com.example.jip.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate ( @Param("classId")int classId,
                                          @Param("date")Date date);

    @Query(" SELECT new com.example.jip.dto.ScheduleDTO("
            + "s.day_of_week, c.id, s.start_time, s.end_time, s.description, s.event, "
            + "s.date, s.location, c.name, c.teacher.id, c.teacher.fullname)"
            + "FROM Schedule s "
            + " JOIN Class c ON s.clasz.id = c.id "
            + " JOIN Teacher t ON c.teacher.id = t.id "
            + "JOIN Listt l ON l.clas.id = s.clasz.id "
            + "WHERE l.student.id = :studentId ")
    List<ScheduleDTO> findStudentSchedule(@Param("studentId") int studentId);


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
