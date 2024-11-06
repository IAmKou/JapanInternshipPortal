package com.example.jip.repository;

import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

}
