package com.example.jip.repository;

import com.example.jip.entity.Application;
import com.example.jip.entity.PersonalMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
}
