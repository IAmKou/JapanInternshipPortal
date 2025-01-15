package com.example.jip.configuration;

import com.example.jip.entity.Exam;
import com.example.jip.repository.ExamRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamInitializer {

    @Autowired
    private ExamRepository examRepository;

    @PostConstruct
    public void initExam() {
        createExamIfNotFound("Exam 1");
        createExamIfNotFound("Exam 2");
        createExamIfNotFound("Exam 3");
        createExamIfNotFound("Exam 4");
        createExamIfNotFound("Exam 5");
        createExamIfNotFound("Exam 6");
        createExamIfNotFound("Exam 7");
        createExamIfNotFound("Exam 8");
        createExamIfNotFound("Exam 9");
        createExamIfNotFound("Exam 10");
        createExamIfNotFound("Exam 11");
        createExamIfNotFound("Exam 12");
        createExamIfNotFound("Exam 13");
        createExamIfNotFound("Exam 14");
        createExamIfNotFound("Exam 15");
        createExamIfNotFound("Exam 16");
        createExamIfNotFound("Exam 17");
        createExamIfNotFound("Exam 18");
        createExamIfNotFound("Exam 19");
        createExamIfNotFound("Exam 20");
        createExamIfNotFound("Exam 21");
        createExamIfNotFound("Exam 22");
        createExamIfNotFound("Exam 23");
        createExamIfNotFound("Exam 24");
        createExamIfNotFound("Exam 25");
        createExamIfNotFound("Exam 26");
        createExamIfNotFound("Exam 27");
        createExamIfNotFound("Exam 28");
        createExamIfNotFound("Exam 29");
        createExamIfNotFound("Exam 30");
        createExamIfNotFound("Exam 31");
        createExamIfNotFound("Exam 32");
        createExamIfNotFound("Exam 33");
        createExamIfNotFound("Exam 34");
        createExamIfNotFound("Exam 35");
        createExamIfNotFound("Exam 36");
        createExamIfNotFound("Exam 37");
        createExamIfNotFound("Exam 38");
        createExamIfNotFound("Exam 39");
        createExamIfNotFound("Exam 40");
        createExamIfNotFound("Exam 41");
        createExamIfNotFound("Exam 42");
        createExamIfNotFound("Exam 43");
        createExamIfNotFound("Exam 44");
    }

    private void createExamIfNotFound(String title) {
        if (examRepository.findByTitle(title) == null) {
            Exam exam = new Exam();
            exam.setTitle(title);
            examRepository.save(exam);
        }
    }
}
