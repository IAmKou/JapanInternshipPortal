package com.example.jip.services;


import com.example.jip.dto.request.exam.ExamCreationRequest;
import com.example.jip.dto.request.exam.ExamUpdateRequest;
import com.example.jip.dto.request.examResult.ExamResultGradeRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.exam.ExamResponse;
import com.example.jip.entity.*;

import com.example.jip.entity.Class;
import com.example.jip.repository.ExamRepository;
import com.example.jip.repository.ExamResultRepository;
import com.example.jip.repository.TeacherRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamSerivce {
    ExamRepository examRepository;
    ExamResultRepository examResultRepository;
    TeacherRepository teacherRepository;

    // Get exams created by a teacher
    @PreAuthorize("hasAuthority('TEACHER')")
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(exam -> {
                    ExamResponse response = new ExamResponse();
                    response.setId(exam.getId());
                    response.setExam_date(exam.getExam_date());
                    response.setExam_name(exam.getExam_name());
                    response.setContent(exam.getContent());
                    response.setTeacherId(exam.getTeacher().getId());
                    response.setBlock(exam.getBlock());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public Exam createExam(ExamCreationRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher ID not found: " + request.getTeacher().getId()));

        // Create Exam entity
        Exam exam = new Exam();
        exam.setExam_name(request.getExam_name());
        exam.setExam_date(request.getExam_date());
        exam.setContent(request.getContent());
        exam.setBlock(request.getBlock());
        exam.setTeacher(teacher);

        // Save the exam
        return examRepository.save(exam);
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public void gradeExam(int examId, List<ExamResultGradeRequest> results) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NoSuchElementException("Exam not found"));

        // Process each result
        for (ExamResultGradeRequest result : results) {
            int studentId = result.getStudentId();
            ExamResult examResult = examResultRepository.findByExamAndStudentId(exam, studentId)
                    .orElseThrow(() -> new NoSuchElementException("ExamResult not found"));

            // Update grade
            examResult.setMark(result.getMark());
            examResultRepository.save(examResult);
        }
    }
    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteExamById(int examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));
        examRepository.delete(exam);
    }
    @PreAuthorize("hasAuthority('TEACHER')")
    public ExamResponse getExamById(int examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setExam_name(exam.getExam_name());
        response.setContent(exam.getContent());
        response.setExam_date(exam.getExam_date());
        response.setBlock(exam.getBlock());

        return response;
    }
    @PreAuthorize("hasAuthority('TEACHER')")
    public void updateAssignment(int examId, ExamUpdateRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with ID: " + examId));

        if(request.getExam_name() != null){
            exam.setExam_name(request.getExam_name());
        }
        if(request.getExam_date()!= null){
            exam.setExam_date(request.getExam_date());
        }
        if(request.getContent()!= null){
            exam.setContent(request.getContent());
        }
        if(request.getBlock() != 0){
            exam.setBlock(request.getBlock());
        }
        examRepository.save(exam);
    }
}