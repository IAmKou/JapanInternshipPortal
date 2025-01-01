package com.example.jip.worker;

import com.example.jip.entity.Semester;
import com.example.jip.repository.SemesterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
public class SemesterStatusScheduler {
    @Autowired
    private SemesterRepository semesterRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void updateSemesterStatus() {
        LocalDate today = LocalDate.now();
        Date todayDate = Date.valueOf(today); // Convert LocalDate to java.sql.Date

        // Activate semesters that start today
        List<Semester> startingSemesters = semesterRepository.findByStartDate(todayDate);
        for (Semester semester : startingSemesters) {
            semester.setStatus(Semester.status.Active);
            semesterRepository.save(semester);
        }

        // Deactivate semesters that end today
        List<Semester> endingSemesters = semesterRepository.findByEndDate(todayDate);
        for (Semester semester : endingSemesters) {
            semester.setStatus(Semester.status.Inactive);
            semesterRepository.save(semester);
        }
    }
}
