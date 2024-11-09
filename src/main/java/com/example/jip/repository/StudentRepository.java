package com.example.jip.repository;

import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    Optional<Student> findById(Integer id);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByPhoneNumber(String phoneNumber);

    @Query("SELECT s FROM Student s WHERE s.id NOT IN (SELECT l.id.student_id FROM List l) ORDER BY s.fullname ASC limit 30")
    List<Student> findTop30UnassignedStudents();


}
