package com.example.jip.repository;

import com.example.jip.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<Teacher> findById(Integer id);
    Optional<Teacher> findByAccount_id(Integer account_id);



}
