package com.example.jip.repository;

import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalMaterialRepository extends JpaRepository<PersonalMaterial, Integer> {
    public List<PersonalMaterial> findByStudent_Id(Integer studentId);

    @Query("SELECT p FROM PersonalMaterial p WHERE p.student.id = :studentId AND p.material_link = :materialLink")
    Optional<PersonalMaterial> findByStudent_IdAndMaterial_link(@Param("studentId") Integer studentId, @Param("materialLink") String materialLink);
}
