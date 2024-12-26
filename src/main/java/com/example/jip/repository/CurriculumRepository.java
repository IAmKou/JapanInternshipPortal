package com.example.jip.repository;

import com.example.jip.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {
    Optional<Curriculum> findById(Integer id);
}
