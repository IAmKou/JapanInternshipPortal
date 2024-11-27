package com.example.jip.controller;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentSubmitRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import com.example.jip.entity.StudentAssignment;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.StudentAssignmentRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentAssignmentServices;
import com.example.jip.services.StudentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-assignment")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentAssignmentController {

    StudentAssignmentServices studentAssignmentServices;

    StudentServices studentServices;

    StudentRepository studentRepository;

    AssignmentRepository assignmentRepository;

    StudentAssignmentRepository studentAssignmentRepository;



    @GetMapping("/list-assignment")
    public ResponseEntity<?> getAssignmentsForStudent(@RequestParam int studentId) {
        try {
            // Fetch all assignments assigned to the student
            List<Assignment> allAssignments = assignmentRepository.findAssignmentsByStudentId(studentId);

            // Fetch the IDs of assignments the student has already submitted
            List<Integer> submittedAssignmentIds = studentAssignmentRepository.findSubmittedAssignmentIdsByStudentId(studentId);

            // Filter out submitted assignments
            List<Assignment> assignmentsToDisplay = allAssignments.stream()
                    .filter(assignment -> !submittedAssignmentIds.contains(assignment.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(assignmentsToDisplay);
        } catch (Exception e) {
            log.error("Error fetching assignments for student: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching assignments.");
        }
    }

    @GetMapping("/list-unsubmitted-assignments")
    public ResponseEntity<List<AssignmentResponse>> getUnsubmittedAssignments(@RequestParam("studentId") int studentId) {
        try {
            List<Assignment> allAssignments = assignmentRepository.findAssignmentsByStudentId(studentId);
            List<Integer> submittedAssignmentIds = studentAssignmentRepository.findSubmittedAssignmentIdsByStudentId(studentId);

            List<AssignmentResponse> unsubmittedAssignments = allAssignments.stream()
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

            return ResponseEntity.ok(unsubmittedAssignments);
        } catch (Exception e) {
            log.error("Error fetching unsubmitted assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/get-student-id")
    public ResponseEntity<Integer> getStudentId(@RequestParam int accountId) {
        int studentId = studentServices.getStudentIdByAccountId(accountId);
        return ResponseEntity.ok(studentId);
    }

    @GetMapping("/list-submitted-assignments")
    public ResponseEntity<List<StudentAssignmentResponse>> getSubmittedAssignments(@RequestParam("studentId") int studentId) {
        try {
            // Fetch all submitted assignments for the student
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
                        response.setAssignment(sa.getAssignment());
                        response.setStudent(sa.getStudent());
                        return response;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching submitted assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PostMapping("/submit")
    public ResponseEntity<?> submitAssignment(@ModelAttribute StudentAssignmentSubmitRequest request,
                                              @RequestParam("student_id") int studentId,
                                              @RequestParam("assignment_id") int assignmentId) {
        try {
            log.info("Received submission request: {}", request);

            // Find student
            Optional<Student> studentOpt = studentRepository.findByAccount_id(studentId);
            if (!studentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
            }
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(studentOpt.get().getId());
            request.setStudent(studentDTO);

            //Find Assignment
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Assignment not found.");
            }
            AssignmentResponse response = new AssignmentResponse();
            response.setId(assignmentOpt.get().getId());
            request.setAssignment(response);


            // Check if the student has already submitted for this assignment
            boolean alreadySubmitted = studentAssignmentRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId);
            if (alreadySubmitted) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already submitted for this assignment.");
            }



            // Call service to submit assignment
            studentAssignmentServices.submitAssignment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Submission successful!");
        } catch (RuntimeException e) {
            log.error("Error during submission: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during submission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}