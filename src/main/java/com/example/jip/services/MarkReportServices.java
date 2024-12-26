package com.example.jip.services;

import com.example.jip.entity.MarkReport;
import com.example.jip.repository.MarkReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
public class MarkReportServices {

    @Autowired
    private MarkReportRepository markReportRepository;

    public List<MarkReport> getMarkReportByStudentId(int studentId) {
        return markReportRepository.findByStudentId(studentId);
    }

    public void saveMarkReport(MarkReport markReport) {
        markReportRepository.save(markReport);
    }

//    public MarkReport updateMarkReport(int id, MarkReport markReport) {
//        // Kiểm tra xem báo cáo điểm có tồn tại không
//        Optional<MarkReport> existingReport = markReportRepository.findById(id);
//        if (existingReport.isPresent()) {
//            MarkReport existingMarkReport = existingReport.get();
//            existingMarkReport.setStudentId(markReport.getStudentId());
//            existingMarkReport.setComment(markReport.getComment());
//            existingMarkReport.setAttendant_rate(markReport.getAttendant_rate());
//            existingMarkReport.setAvg_assignment_mark(markReport.getAvg_assignment_mark());
//            existingMarkReport.setAvg_exam_mark(markReport.getAvg_exam_mark());
//            existingMarkReport.setReading_mark(markReport.getReading_mark());
//            existingMarkReport.setListening_mark(markReport.getListening_mark());
//            existingMarkReport.setSpeaking_mark(markReport.getSpeaking_mark());
//
//            return markReportRepository.save(existingMarkReport);
//        } else {
//            throw new RuntimeException("Cannot find MarkReport with id " + id);
//        }
//    }
//    public BigDecimal calculateCourseTotal(int studentId) {
//        Optional<MarkReport> reportOpt = getMarkReportByStudentId(studentId);
//        if (reportOpt.isPresent()) {
//            MarkReport markReport = reportOpt.get();
//            return markReport.getCourseTotal(); // sử dụng phương thức getCourseTotal() từ MarkReport
//        } else {
//            throw new RuntimeException("Cannot find MarkReport for student with id " + studentId);
//        }
//    }
}
