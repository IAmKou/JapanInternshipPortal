package com.example.jip.repository;

import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.ListId;
import com.example.jip.entity.Listt;
import com.example.jip.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListRepository extends JpaRepository<Listt, ListId> {
    @Query("SELECT l.student FROM Listt l WHERE l.clas.id = :classId")
    List<Student> findStudentsByClassId(int classId);
}
