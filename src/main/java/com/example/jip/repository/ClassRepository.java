package com.example.jip.repository;

import com.example.jip.entity.Semester;
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
    @Query("select c from Class c where c.teacher.id = :teacherId and c.semester.status = :status")
    List<Class> findByTeacher_IdAndSemester(@Param("teacherId") Integer teacherId, @Param("status") Semester.status status);
    Optional<Class> findById(Integer id);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Class c WHERE c.name = :name AND c.semester.id = :semesterId")
    boolean existsByNameAndSemesterId(@Param("name") String name, @Param("semesterId") int semesterId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Class c WHERE c.name = :name AND c.semester.id = :semesterId AND c.id != :id")
    boolean existsByNameAndSemesterIdAndIdNot(@Param("name") String name,
                                              @Param("semesterId") int semesterId,
                                              @Param("id") int id);
    @Query("SELECT c FROM Class c WHERE c.name = :name AND c.semester.id = :semesterId")
    Class findByNameAndSemesterId(@Param("name") String name, @Param("semesterId") int semesterId);

    @Query("SELECT c FROM Class c WHERE c.semester.id = :semesterId")
    List<Class> findBySemesterId(@Param("semesterId") int semesterId);
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
