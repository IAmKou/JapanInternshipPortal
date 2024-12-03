package com.example.jip.services;



import com.example.jip.dto.request.studentAssignment.StudentAssignmentSubmitRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentUpdateRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
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

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentAssignmentServices {


    StudentAssignmentRepository studentAssignmentRepository;

    AssignmentRepository assignmentRepository;

    StudentRepository studentRepository;

    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<StudentAssignment> getAllStudentAssignments(){
        return studentAssignmentRepository.findAll();
    }


    @PreAuthorize("hasAuthority('STUDENT')")
    public List<AssignmentResponse> getAssignmentsForStudent(int studentId) {
        log.info("Fetching assignments for student ID: {}", studentId);
        List<Assignment> allAssignments = assignmentRepository.findAssignmentsByStudentId(studentId);
        List<Integer> submittedAssignmentIds = studentAssignmentRepository.findSubmittedAssignmentIdsByStudentId(studentId);

        return allAssignments.stream()
                .filter(assignment -> !submittedAssignmentIds.contains(assignment.getId()))
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setDescription(assignment.getDescription());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
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
        studentAssignment.setStatus(StudentAssignment.Status.SUBMITTED);
        studentAssignment.setAssignment(assignment);

        return studentAssignmentRepository.save(studentAssignment);
    }

    public List<StudentAssignmentResponse> getSubmittedAssignmentByStudentId(int studentId) {
        List<StudentAssignment> studentAssignments = studentAssignmentRepository.findByStudentId(studentId);;

        // Map to DTOs
         List<StudentAssignmentResponse> responses = studentAssignments.stream()
                .map(sa -> {
                    StudentAssignmentResponse response = new StudentAssignmentResponse();
                    response.setId(sa.getId());
                    response.setMark(sa.getMark());
                    response.setDescription(sa.getDescription());
                    response.setContent(sa.getContent());
                    response.setDate(sa.getDate());
                    response.setStatus(sa.getStatus().toString());
                    response.setAssignmentId(sa.getAssignment().getId());
                    response.setStudentId(sa.getStudent().getId());
                    response.setAssignmentName(sa.getAssignment().getDescription());
                    return response;
                })
                .collect(Collectors.toList());
         return responses;
    }

    public StudentAssignmentResponse getStudentAssignmentDetail(int studentAssignmentId) {
        // Fetch the StudentAssignment entity from the repository
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(studentAssignmentId)
                .orElseThrow(() -> new NoSuchElementException("StudentAssignment not found"));

        // Map the entity to the response DTO
        StudentAssignmentResponse response = new StudentAssignmentResponse();
        response.setId(studentAssignment.getId());
        response.setDescription(studentAssignment.getDescription());
        response.setContent(studentAssignment.getContent());
        response.setDate(studentAssignment.getDate());
        response.setMark(studentAssignment.getMark());
        response.setStatus(studentAssignment.getStatus().toString());
        response.setAssignmentId(studentAssignment.getAssignment().getId()); // Includes assignment details
        response.setStudentId(studentAssignment.getStudent().getId()); // Includes student details
        return response;
    }

    public AssignmentResponse getAssignmentByStudentAssignmentId(int studentAssignmentId) {
        // Find the StudentAssignment by ID
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(studentAssignmentId)
                .orElseThrow(() -> new NoSuchElementException("StudentAssignment not found"));

        // Get the related Assignment
        Assignment assignment = studentAssignment.getAssignment();

        if (assignment == null) {
            throw new NoSuchElementException("Assignment not found for this StudentAssignment");
        }

        // Map Assignment to AssignmentResponse
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setDescription(assignment.getDescription());
        response.setContent(assignment.getContent());
        response.setCreated_date(assignment.getCreated_date());
        response.setEnd_date(assignment.getEnd_date());
        response.setClasses(assignment.getClasses().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));
        String folderName = assignment.getImgUrl();
        try {
            List<Map<String, Object>> resources = cloudinaryService.getFilesFromFolder(folderName);
            List<String> fileUrls = resources.stream()
                    .map(resource -> (String) resource.get("url"))
                    .collect(Collectors.toList());

            if (fileUrls.isEmpty()) {
                log.warn("No files found for assignment with ID: {}", response.getId());
            }
            response.setFiles(fileUrls);  // Assuming these are the file URLs

        } catch (Exception e) {
            log.error("Error retrieving files for assignment with ID: {}", response.getId(), e);
            response.setFiles(Collections.emptyList());
        }
        return response;
    }
    @PreAuthorize("hasAuthority('STUDENT')")
    public StudentAssignment updateStudentAssignment(int submittedAssignmentId, StudentAssignmentUpdateRequest request) {
        StudentAssignment existingAssignment = studentAssignmentRepository.findById(submittedAssignmentId)
                .orElseThrow(() -> new RuntimeException("StudentAssignment not found"));

        if (existingAssignment.getStatus() == StudentAssignment.Status.MARKED) {
            throw new RuntimeException("Cannot edit a marked assignment.");
        }

        existingAssignment.setDescription(request.getDescription());
        existingAssignment.setContent(request.getContent());
        existingAssignment.setDate(new Date());

        return studentAssignmentRepository.save(existingAssignment);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public void deleteStudentAssignment(int submittedAssignmentId) {
        StudentAssignment existingAssignment = studentAssignmentRepository.findById(submittedAssignmentId)
                .orElseThrow(() -> new RuntimeException("StudentAssignment not found"));

        if (existingAssignment.getStatus() == StudentAssignment.Status.MARKED) {
            throw new RuntimeException("Cannot delete a marked assignment.");
        }

        studentAssignmentRepository.delete(existingAssignment);
    }

}
