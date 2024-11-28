package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.exam.ExamCreationRequest;
import com.example.jip.dto.request.examResult.ExamResultGradeRequest;
import com.example.jip.entity.Exam;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ExamSerivce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
@Slf4j
public class ExamController {
     ExamSerivce examService;

     TeacherRepository teacherRepository;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createExam(@ModelAttribute ExamCreationRequest request,
                                              @RequestParam("teacher_id") int teacherId){
        try {
            log.info("Received request: " + request);

            Optional<Teacher> teacherOpt = teacherRepository.findByAccount_id(teacherId);
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacherOpt.get().getId());
            request.setTeacher(teacherDTO);

            examService.createExam(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            throw new RuntimeException("Failed to create assignment", e);
        }
    }

    @GetMapping("/list-exams")
    public ResponseEntity<List<Exam>> getAllExam() {
        List<Exam> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
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
