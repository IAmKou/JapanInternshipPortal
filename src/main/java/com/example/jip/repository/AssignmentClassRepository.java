package com.example.jip.repository;

import com.example.jip.entity.AssignmentClass;
import com.example.jip.entity.AssignmentClassId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentClassRepository extends JpaRepository<AssignmentClass, Class> {
}
