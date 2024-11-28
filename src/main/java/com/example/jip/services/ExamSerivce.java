package com.example.jip.services;

import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.exam.ExamCreationRequest;
import com.example.jip.dto.request.examResult.ExamResultGradeRequest;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.ExamRepository;
import com.example.jip.repository.ExamResultRepository;
import com.example.jip.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

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
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public Exam createExam(ExamCreationRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher ID not found: " + request.getTeacher().getId()));

        // Create Exam entity
        Exam exam = new Exam();
        exam.setExam_name(request.getExam_name());
        exam.setExam_date(request.getExam_date());
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
}
