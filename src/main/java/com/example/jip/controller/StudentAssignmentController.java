package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentAssignmentServices;
import com.example.jip.services.StudentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student-assignment")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentAssignmentController {

    StudentAssignmentServices studentAssignmentServices;

    StudentServices studentServices;


    @GetMapping("/list-assignment")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsForStudent(@RequestParam int studentId) {
        log.info("Fetching assignments for student ID: {}", studentId);
        List<AssignmentResponse> assignments = studentAssignmentServices.getAssignmentsForStudent(studentId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/get-student-id")
    public ResponseEntity<Integer> getStudentId(@RequestParam int accountId) {
        int studentId = studentServices.getStudentIdByAccountId(accountId);
        return ResponseEntity.ok(studentId);
    }
}
