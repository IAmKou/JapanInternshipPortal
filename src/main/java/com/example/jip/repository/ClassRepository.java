package com.example.jip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jip.entity.Class;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class,Integer> {
    int countByTeacherId(Integer teacherId);

    List<Class> findByTeacher_Id(Integer teacherId);
    Optional<Class> findByName(String name);
    Optional<Class> findById(Integer id);
    boolean existsByName(String name);
    @Modifying
    @Query("UPDATE Class c SET c.status = 'Active' WHERE c.semester.id = :semesterId")
    void activateClassesBySemester(int semesterId);

    @Modifying
    @Query("UPDATE Class c SET c.status = 'Inactive' WHERE c.semester.id = :semesterId")
    void deactivateClassesBySemester(int semesterId);
}
