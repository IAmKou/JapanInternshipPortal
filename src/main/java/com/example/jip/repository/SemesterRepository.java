package com.example.jip.repository;

import com.example.jip.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    boolean existsByName(String name);
    Optional<Semester> findById(int id);
}
