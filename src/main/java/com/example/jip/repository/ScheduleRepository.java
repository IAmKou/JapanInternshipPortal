package com.example.jip.repository;

import com.example.jip.dto.ClassScheduleDTO;
import com.example.jip.dto.ScheduleAttendanceDTO;
import com.example.jip.entity.Schedule;
import com.example.jip.entity.Semester;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId AND s.date = :date")
    List<Schedule> findByClassIdAndDate(@Param("classId") int classId,
                                        @Param("date") Date date);

    Optional<Schedule> findByDateAndSemester(java.sql.Date date, Semester semester);  // Check if schedule for a holiday already exists

    boolean existsByClaszIdAndSemesterId(int classId, int semesterId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s " +
            "WHERE s.room = :room AND s.semester.id = :semesterId AND s.clasz.id != :classId")
    boolean existsByRoomAndSemesterIdAndClassNot(@Param("room") String room,
                                                 @Param("semesterId") int semesterId,
                                                 @Param("classId") int classId);

    List<Schedule> findBySemesterIdAndStatus(int semesterId, Schedule.status status);


    @Query("SELECT s FROM Schedule s WHERE s.semester.id = :semesterId AND s.date = :date AND s.activity = :activity AND s.clasz.id = null")
   Schedule findBySemesterIdAndDateAndActivity(@Param("semesterId") int semesterId,
                                                @Param("date") java.sql.Date date,
                                                @Param("activity") String activity);

    @Query("SELECT s FROM Schedule s WHERE s.semester.id = :semesterId AND s.date = :date AND s.activity = :activity AND s.clasz.id = :classId")
    Schedule findBySemesterIdAndDateAndActivityAndClasz(@Param("semesterId") int semesterId,
                                                         @Param("date") java.sql.Date date,
                                                         @Param("activity") String activity,
                                                         @Param("classId") int classId);

    @Query("SELECT new com.example.jip.dto.ClassScheduleDTO(c.id, c.name, MAX(s.room), MAX(sem.name), MAX(sem.id)) " +
            "FROM Schedule s " +
            "JOIN s.clasz c " +
            "JOIN s.semester sem " +
            "GROUP BY c.id, c.name, sem.id")
    List<ClassScheduleDTO> findUniqueClassSchedule();

    @Query("SELECT s FROM Schedule s WHERE s.clasz.id = :classId")
    List<Schedule> findByClaszId(@Param("classId") int classId);

    @Query(value = """
    SELECT\s
        CAST(s.id AS SIGNED) AS scheduleId,
        s.date AS scheduleDate,
        s.day_of_week AS dayOfWeek,
        s.room AS room,
        s.activity AS activity,
        CAST(st.id AS SIGNED) AS studentId,
        a.status AS attendanceStatus,
        s.color AS color
    FROM schedule s
    JOIN class c ON s.class_id = c.id
    JOIN list l ON c.id = l.class_id
    JOIN student st ON l.student_id = st.id
    LEFT JOIN attendant a ON a.schedule_id = s.id AND a.student_id = st.id
    WHERE c.id = :classId AND st.id = :studentId
""", nativeQuery = true)
    List<Object[]> findSchedulesWithAttendanceByClassIdNative(
            @Param("classId") int classId,
            @Param("studentId") int studentId
    );


    boolean existsBySemesterIdAndDateAndRoom(int semesterId, java.sql.Date date, String room);

    List<Schedule> findAllByDate(LocalDate now);

    List<Schedule> findAllByDateLessThanEqual(LocalDate date);
}
