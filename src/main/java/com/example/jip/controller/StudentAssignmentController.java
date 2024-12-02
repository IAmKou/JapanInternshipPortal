package com.example.jip.controller;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentSubmitRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentUpdateRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.StudentAssignmentRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentAssignmentServices;
import com.example.jip.services.StudentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsForStudent(@RequestParam("studentId") int studentId) {
        try {
            List<AssignmentResponse> response = studentAssignmentServices.getAssignmentsForStudent(studentId);
            return ResponseEntity.ok(response);
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

            List<StudentAssignmentResponse> responses = studentAssignmentServices.getSubmittedAssignmentByStudentId(studentId);

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching submitted assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/list-submitted-assignments-student")
    public ResponseEntity<List<StudentAssignmentResponse>> getSubmittedAssignmentsByStudentId(@RequestParam("studentId") int studentId) {
        try {

            List<StudentAssignmentResponse> responses = studentAssignmentServices.getSubmittedAssignmentByStudentId(studentId);

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching submitted assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<StudentAssignmentResponse> getStudentAssignmentDetail(
            @RequestParam int studentAssignmentId) {
        try {
            StudentAssignmentResponse response = studentAssignmentServices.getStudentAssignmentDetail(studentAssignmentId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Return 404 if StudentAssignment is not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Return 500 for any other errors
        }
    }

    @GetMapping("/assignment/{studentAssignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentByStudentAssignmentId(
            @PathVariable int studentAssignmentId) {
        try {
            AssignmentResponse response = studentAssignmentServices.getAssignmentByStudentAssignmentId(studentAssignmentId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Return 404 if StudentAssignment or Assignment is not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Return 500 for unexpected errors
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

    @PutMapping("/update/{submittedAssignmentId}")
    public ResponseEntity<?> updateSubmittedAssignment(@PathVariable("submittedAssignmentId") int submittedAssignmentId,
                                              @ModelAttribute StudentAssignmentUpdateRequest request) {
        try {
            log.info("Received request: " + request);  // Log the incoming request for debugging

            studentAssignmentServices.updateStudentAssignment(submittedAssignmentId, request);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful update
        } catch (NoSuchElementException e) {
            log.error("StudentAssignment not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if doesn't exist
        }
    }

    @DeleteMapping("/delete/{submittedAssignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable("submittedAssignmentId") int submittedAssignmentId) {
        try {
            log.info("Received delete request for SubmittedAssignmentId: {}", submittedAssignmentId);
            studentAssignmentServices.deleteStudentAssignment(submittedAssignmentId);
            return ResponseEntity.ok("Assignments deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting assignments.");
        }
    }


}