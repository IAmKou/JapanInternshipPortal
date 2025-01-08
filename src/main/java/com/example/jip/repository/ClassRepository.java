package com.example.jip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jip.entity.Class;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class,Integer> {
    List<Class> findByTeacher_Id(Integer teacherId);
    Optional<Class> findById(Integer id);
    boolean existsByNameAndStatus(String name, Class.status status);
    @Modifying
    @Query("UPDATE Class c SET c.status = 'Active' WHERE c.semester.id = :semesterId")
    void activateClassesBySemester(int semesterId);

    @Modifying
    @Query("UPDATE Class c SET c.status = 'Inactive' WHERE c.semester.id = :semesterId")
    void deactivateClassesBySemester(int semesterId);

    @Query("SELECT c FROM Class c JOIN c.classLists l WHERE l.student.id = :studentId")
    List<Class> findClassesByStudentId(@Param("studentId") int studentId);

    @Query("SELECT c.semester.start_time, c.semester.end_time FROM Class c WHERE c.id = :classId")
    Object[] findSemesterStartAndEndByClassId(@Param("classId") int classId);

    @Query("SELECT COUNT(c) FROM Class c " +
            "WHERE c.teacher.id = :teacherId " +
            "AND c.semester.id = :semesterId")
    int countClassesByTeacherAndSemester(@Param("teacherId") int teacherId,
                                         @Param("semesterId") int semesterId);


}
