package com.example.jip.services;



import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Class;
import com.example.jip.entity.Student;
import com.example.jip.entity.StudentAssignment;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.StudentAssignmentRepository;
import com.example.jip.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentAssignmentServices {


    StudentAssignmentRepository studentAssignmentRepository;

    AssignmentRepository assignmentRepository;

    StudentRepository studentRepository;

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<StudentAssignment> getAllStudentAssignments(){
        return studentAssignmentRepository.findAll();
    }

    public List<Assignment> getAssignmentsForStudent(int studentId) {
        return assignmentRepository.findAssignmentsByStudentId(studentId);
    }

}
