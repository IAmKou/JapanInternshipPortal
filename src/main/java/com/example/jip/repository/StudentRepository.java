package com.example.jip.repository;

import com.example.jip.entity.Student;
import com.example.jip.dto.StudentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findById(Integer id);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByPhoneNumber(String phoneNumber);
    Optional<Student> findByAccount_id(Integer account_id);

    @Query("SELECT s.id FROM Student s WHERE s.account.id = :accountId")
    Optional<Integer> findStudentIdByAccountId(@Param("accountId") int accountId);

    @Query("SELECT new com.example.jip.dto.StudentDTO(s) " +
            "FROM Student s WHERE s.id NOT IN (SELECT l.id.student_id FROM Listt l) " +
            "ORDER BY s.fullname ASC limit 30")
    List<StudentDTO> findTop30UnassignedStudents();



}
