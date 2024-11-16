package com.example.jip.repository;

import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalMaterialRepository extends JpaRepository<PersonalMaterial, Integer> {
    public List<PersonalMaterial> findByStudent_Id(Integer studentId);
}
