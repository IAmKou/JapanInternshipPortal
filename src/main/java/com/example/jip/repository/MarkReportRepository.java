package com.example.jip.repository;

import com.example.jip.entity.MarkReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkReportRepository extends CrudRepository<MarkReport, Integer> {
    List<MarkReport> findByStudentId(int studentId);
}
