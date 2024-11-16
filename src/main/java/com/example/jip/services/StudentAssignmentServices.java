package com.example.jip.services;



import com.example.jip.entity.StudentAssignment;
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



    @PreAuthorize("hasAuthority('STUDENT')")
    public List<StudentAssignment> getAllStudentAssignments(){
        return studentAssignmentRepository.findAll();
    }

}
