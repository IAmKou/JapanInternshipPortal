package com.example.jip.services;


import com.example.jip.dto.response.StudentAssignmentResponse;
import com.example.jip.mapper.StudentAssignmentMapper;
import com.example.jip.repository.StudentAssignmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentAssignmentServices {


    StudentAssignmentRepository studentAssignmentRepository;

    StudentAssignmentMapper studentAssignmentMapper;

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<StudentAssignmentResponse> getAllStudentAssignments(){
        return studentAssignmentRepository.findAll().stream()
                .map(studentAssignmentMapper::toStudentAssignmentResponse).toList();
    }



}
