package com.example.jip.repository;

import com.example.jip.entity.Curriculum;
import com.example.jip.entity.CurriculumInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumInformationRepository extends JpaRepository<CurriculumInformation, Integer> {
    CurriculumInformation findByCurriculum(Curriculum curriculum);
}
