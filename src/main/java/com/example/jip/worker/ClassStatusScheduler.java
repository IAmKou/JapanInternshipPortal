package com.example.jip.worker;

import com.example.jip.entity.Semester;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.SemesterRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
@Slf4j
@Component
public class ClassStatusScheduler {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ClassRepository classRepository;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void updateClassStatus() {
        List<Semester> relevantSemesters  = semesterRepository.findRelevantSemesters();
        for (Semester semester : relevantSemesters) {
            LocalDate today = LocalDate.now();
            Date todayDate = Date.valueOf(today);

            // Activate classes if the semester is overdue for activation
            if (semester.getStart_time().before(todayDate) && semester.getStatus() != Semester.status.Active) {
                classRepository.activateClassesBySemester(semester.getId());
                semester.setStatus(Semester.status.Active); // Update semester status
                semesterRepository.save(semester); // Save the updated status
                log.info("Activated classes for semester: " + semester.getId());
            }

            // Deactivate classes if the semester is overdue for deactivation
            if (semester.getEnd_time().before(todayDate) && semester.getStatus() != Semester.status.Inactive) {
                classRepository.deactivateClassesBySemester(semester.getId());
                semester.setStatus(Semester.status.Inactive); // Update semester status
                semesterRepository.save(semester); // Save the updated status
                log.info("Deactivated classes for semester: " + semester.getId());
            }
        }
        }
}
