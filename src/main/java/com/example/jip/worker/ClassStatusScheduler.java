package com.example.jip.worker;

import com.example.jip.entity.Semester;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.SemesterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
public class ClassStatusScheduler {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ClassRepository classRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void updateClassStatus() {
        // Fetch semesters with start or end date matching today
        List<Semester> currentSemesters = semesterRepository.findSemestersByCurrentDate();

        for (Semester semester : currentSemesters) {
            LocalDate today = LocalDate.now();
            Date todayDate = Date.valueOf(today);
            if ( semester.getStart_time().equals(todayDate)) {
                // Activate classes for the semester
                classRepository.activateClassesBySemester(semester.getId());
            }
            if ( semester.getEnd_time().equals(todayDate)) {
                // Deactivate classes for the semester
                classRepository.deactivateClassesBySemester(semester.getId());
            }
        }
    }
}
