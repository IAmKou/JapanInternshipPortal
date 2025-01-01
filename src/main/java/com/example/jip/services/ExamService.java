package com.example.jip.services;

import com.example.jip.entity.Exam;
import com.example.jip.repository.ExamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ExamService {

    ExamRepository examRepository;
    public void createExams(int n) {
        for (int i = 0; i < n; i++) {
            Exam exam = new Exam();
            exam.setTitle("Exam " + (i + 1));
            log.info("Created Exam: {}", exam.getTitle());
            examRepository.save(exam); // Save each exam
        }
    }
}
