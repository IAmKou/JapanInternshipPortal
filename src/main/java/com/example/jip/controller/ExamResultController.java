package com.example.jip.controller;

import com.example.jip.dto.request.examResult.ExamResultUpdateRequest;
import com.example.jip.dto.response.examResult.ExamResultResponse;
import com.example.jip.exception.NotFoundException;
import com.example.jip.services.ExamResultService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examResult")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamResultController {

    ExamResultService examResultService;

    @GetMapping("/list-exam-results")
    public ResponseEntity<List<ExamResultResponse>> getExamResultByExamId(
            @RequestParam("examId") int examId) {
        try {
            List<ExamResultResponse> submittedAssignments = examResultService.getExamResultByExamId(examId);
            return ResponseEntity.ok(submittedAssignments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return a 404 if examResult not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/list-exam-results-student")
    public ResponseEntity<List<ExamResultResponse>> getExamResultByStudentId(
            @RequestParam("studentId") int studentId) {
        try {
            List<ExamResultResponse> submittedAssignments = examResultService.getExamResultByStudentId(studentId);
            return ResponseEntity.ok(submittedAssignments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return a 404 if examResult not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> gradeExam(@RequestParam("examResultId") int examResultId,
                                       @RequestBody ExamResultUpdateRequest request) {
        try {
            examResultService.updateExamResult(examResultId, request);
            return ResponseEntity.ok("Grades submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while submitting grades.");
        }
    }
}
