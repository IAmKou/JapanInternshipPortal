package com.example.jip.services;

import com.example.jip.entity.MarkReport;
import com.example.jip.repository.MarkReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MarkReportServices {

    @Autowired
    private MarkReportRepository markReportRepository;

    public Optional<MarkReport> getMarkReportByStudentId(int studentId) {
        return markReportRepository.findByStudentId(studentId);
    }

    public void saveMarkReport(MarkReport markReport) {
        markReportRepository.save(markReport);
    }

    public MarkReport updateMarkReport(MarkReport markReport) {
        return markReportRepository.save(markReport);
    }
}
