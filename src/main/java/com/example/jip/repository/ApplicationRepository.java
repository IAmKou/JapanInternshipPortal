package com.example.jip.repository;

import com.example.jip.entity.Application;
import com.example.jip.entity.PersonalMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    public List<Application> findByTeacher_Id(Integer teacherId);
    public List<Application> findByStudent_Id(Integer studentId);
}
