package com.example.jip.repository;

import com.example.jip.entity.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    Optional<Student> findByAccountId(Integer account_id);

}
