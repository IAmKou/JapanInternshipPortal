package com.example.jip.repository;

import com.example.jip.dto.AttendantDTO;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface AttendantRepository extends JpaRepository<Attendant, Integer> {

    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date ORDER BY s.id ASC")
    Optional<Schedule> findByClassAndDate(@Param("classId")int classId, @Param("date")Date date);

    @Query("SELECT a FROM Attendant a WHERE a.student.id = :studentId AND a.schedule.id = :scheduleId AND a.date = :date")
    Optional<Attendant> findByStudentIdAndScheduleIdAndDate(int studentId, int scheduleId, Date date);

    @Query("SELECT new com.example.jip.dto.AttendantDTO( " +
            "   a.id ,a.status,a.date,s.id,sch.id, s.fullname , s.mark ,s.img )" +
            "FROM \n" +
            "    Attendant a\n" +
            "JOIN \n" +
            "    Student s ON a.student.id = s.id\n" +
            "JOIN \n" +
            "    Schedule sch ON a.schedule.id = sch.id\n" +
            "JOIN \n" +
            "    Class c ON sch.clasz.id = c.id\n" +
            "WHERE \n" +
            "    c.id = :classId AND   \n" +
            "    a.date = :date")
    List<AttendantDTO> findByDateAndClassId(
            @Param("date") Date date,
            @Param("classId") int classId );

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Attendant a " +
            "JOIN Schedule s ON a.schedule.id = s.id " +
            "WHERE s.clasz.id = :classId AND s.date = :date")
    boolean existsByClassIdAndDate(@Param("classId") Integer classId, @Param("date") Date date);

    int countAttendedByStudentId(Student student);

    @Query("SELECT c.total_slot " +
            "FROM Attendant a " +
            "JOIN a.curriculum c " +
            "WHERE a.student.id = :studentId")
    Integer findTotalSlotByStudentId(@Param("studentId") Integer studentId);
}
