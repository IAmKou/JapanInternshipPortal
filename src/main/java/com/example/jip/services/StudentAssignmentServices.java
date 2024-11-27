package com.example.jip.services;



import com.example.jip.dto.request.studentAssignment.StudentAssignmentSubmitRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentAssignmentServices {


    StudentAssignmentRepository studentAssignmentRepository;

    AssignmentRepository assignmentRepository;

    StudentRepository studentRepository;

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<StudentAssignment> getAllStudentAssignments(){
        return studentAssignmentRepository.findAll();
    }


    @PreAuthorize("hasAuthority('STUDENT')")
    public List<AssignmentResponse> getAssignmentsForStudent(int studentId) {
        log.info("Fetching assignments for student ID: {}", studentId);
        List<Assignment> assignments = assignmentRepository.findAssignmentsByStudentId(studentId);

        return assignments.stream()
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
                    response.setDescription(assignment.getDescription());
                    response.setContent(assignment.getContent());
                    response.setFolder(assignment.getImgUrl());
                    response.setClasses(
                            assignment.getClasses().stream()
                                    .map(Class::getName)
                                    .collect(Collectors.toList())
                    );
                    return response;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public StudentAssignment submitAssignment(StudentAssignmentSubmitRequest request){
        Student student = studentRepository.findById(request.getStudent().getId())
               .orElseThrow(() -> new RuntimeException("Student ID not found: " + request.getStudent().getId()));

        Assignment assignment = assignmentRepository.findById(request.getAssignment().getId())
               .orElseThrow(() -> new RuntimeException("Assignment ID not found: " + request.getAssignment().getId()));

        Date today = new Date();
        if (today.after(assignment.getEnd_date())) {
            throw new RuntimeException("Submission is not allowed after the assignment's end date.");
        }

        StudentAssignment studentAssignment = new StudentAssignment();
        studentAssignment.setDate(today);
        studentAssignment.setDescription(request.getDescription());
        studentAssignment.setContent(request.getContent());
        studentAssignment.setStudent(student);
        studentAssignment.setAssignment(assignment);

        return studentAssignmentRepository.save(studentAssignment);
    }


}
