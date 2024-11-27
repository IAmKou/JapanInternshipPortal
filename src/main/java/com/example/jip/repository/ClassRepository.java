package com.example.jip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jip.entity.Class;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class,Integer> {
    int countByTeacherId(Integer teacherId);

    List<Class> findByTeacher_Id(Integer teacherId);
    Optional<Class> findByName(String name);
    Optional<Class> findById(Integer id);


}
