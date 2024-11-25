package com.example.jip.controller;

import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.services.StudentAssignmentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/student-assignment")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentAssignmentController {

    StudentAssignmentServices studentAssignmentServices;


    @GetMapping("/list-assignment/{studentId}")
    public ResponseEntity<List<Assignment>> getAssignmentsForStudent(@PathVariable int studentId) {
        List<Assignment> assignments = studentAssignmentServices.getAssignmentsForStudent(studentId);
        return ResponseEntity.ok(assignments);
    }


}
