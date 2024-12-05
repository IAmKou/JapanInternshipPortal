package com.example.jip.services;

import com.example.jip.dto.request.examResult.ExamResultUpdateRequest;
import com.example.jip.dto.response.examResult.ExamResultResponse;
import com.example.jip.entity.*;


import com.example.jip.repository.ExamResultRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamResultService {

    ExamResultRepository examResultRepository;

    public List<ExamResultResponse> getExamResultByExamId(int examId) {
        List<ExamResult> examResults = examResultRepository.findAllByExamId(examId);
        if(examResults == null){
            throw new NoSuchElementException("Exam Result not found");
        } else {
            List<ExamResultResponse> responses = examResults.stream()
                    // Map to DTOs
                    .map(es -> {
                        ExamResultResponse response = new ExamResultResponse();
                        response.setId(es.getId());
                        response.setStudentId(es.getStudent().getId());
                        response.setExamId(es.getExam().getId());
                        response.setMark(es.getMark());
                        response.setStudentName(es.getStudent().getFullname());
                        return response;
                    })
                    .collect(Collectors.toList());
            return responses;
        }
    }

    public List<ExamResultResponse> getExamResultByStudentId(int studentId) {
        List<ExamResult> examResults = examResultRepository.findAllByStudentId(studentId);
        if(examResults == null){
            throw new NoSuchElementException("Exam Result not found");
        } else {
            List<ExamResultResponse> responses = examResults.stream()
                    // Map to DTOs
                    .map(es -> {
                        ExamResultResponse response = new ExamResultResponse();
                        response.setId(es.getId());
                        response.setStudentId(es.getStudent().getId());
                        response.setExamId(es.getExam().getId());
                        response.setMark(es.getMark());
                        response.setStudentName(es.getStudent().getFullname());
                        response.setExamName(es.getExam().getExam_name());
                        return response;
                    })
                    .collect(Collectors.toList());
            return responses;
        }
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public void updateExamResult(int examResultId, ExamResultUpdateRequest request) {
        ExamResult examResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> new NoSuchElementException("Exam Result not found"));

            // Update grade
            if(request != null){
               examResult.setMark(request.getMark());
            }
            examResultRepository.save(examResult);
    }
}