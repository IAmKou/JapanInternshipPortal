package com.example.jip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jip.entity.Class;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class,Integer> {
    int countByTeacherId(Integer teacherId);

    List<Class> findByTeacherId(Integer teacherId);
}
