package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.exam.ExamCreationRequest;
import com.example.jip.dto.request.exam.ExamUpdateRequest;
import com.example.jip.dto.request.examResult.ExamResultGradeRequest;
import com.example.jip.dto.response.exam.ExamResponse;

import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ExamSerivce;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamController {
     ExamSerivce examService;

     TeacherRepository teacherRepository;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createExam(@ModelAttribute ExamCreationRequest request,
                                        @RequestParam("teacher_id") int teacherId) {
        log.info("UserId: " + teacherId);
        try {
            Optional<Teacher> teacherOpt = teacherRepository.findByAccount_id(teacherId);
            log.info("Request: " + request);
            if (teacherOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Teacher not found for account ID: " + teacherId);
            }

            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacherOpt.get().getId());
            request.setTeacher(teacherDTO);

            examService.createExam(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (NullPointerException e) {
            log.error("Repository not injected properly: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: Required dependencies are not configured properly.");
        } catch (Exception e) {
            log.error("Error creating exam: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create exam: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<ExamResponse>> getAllExam() {
        List<ExamResponse> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
    }

    @DeleteMapping("/delete/{exam_id}")
    public ResponseEntity<?> deleteExam(@PathVariable("exam_id") int exam_id) {
        try {
            examService.deleteExamById(exam_id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if exam doesn't exist
        }
    }


    @GetMapping("/detail/{exam_id}")
    public ResponseEntity<ExamResponse> getAssignmentById(@PathVariable("exam_id") int examId) {
        log.info("Received examId: " + examId);
        ExamResponse response = examService.getExamById(examId);
        if (response != null)  {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/update/{exam_id}")
    public ResponseEntity<?> updateExam(@PathVariable("exam_id") int exam_id,
                                              @ModelAttribute ExamUpdateRequest request) {
        try {
            log.info("Received request: " + request);  // Log the incoming request for debugging

            examService.updateAssignment(exam_id, request);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful update
        } catch (NoSuchElementException e) {
            log.error("Assignment not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if exam doesn't exist
        }
    }


    @PutMapping("/grade/{examId}")
    public ResponseEntity<?> gradeExam(@PathVariable int examId,
                                       @RequestBody List<ExamResultGradeRequest> results) {
        try {
            examService.gradeExam(examId, results);
            return ResponseEntity.ok("Grades submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while submitting grades.");
        }
    }
}
