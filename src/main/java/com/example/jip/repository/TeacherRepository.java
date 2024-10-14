package com.example.jip.repository;

import com.example.jip.entity.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeacherRepository extends CrudRepository<Teacher, Integer> {
    Optional<Teacher> findByAccountId(Integer account_id);
}
