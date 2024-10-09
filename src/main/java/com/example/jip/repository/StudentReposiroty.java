package com.example.jip.repository;

import com.example.jip.entity.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentReposiroty extends CrudRepository<Student, Integer> {
}
