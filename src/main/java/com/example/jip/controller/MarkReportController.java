package com.example.jip.controller;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.entity.MarkReport;
import com.example.jip.services.MarkReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MarkReportController {

    @Autowired
    private MarkReportServices markReportService;

    // Hiển thị báo cáo điểm của sinh viên theo ID
    @GetMapping("/mark-report")
    public String viewMarkReport(@RequestParam int studentId, Model model) {
        Optional<MarkReport> markReport = markReportService.getMarkReportByStudentId(studentId);
        if (markReport.isPresent()) {
            model.addAttribute("markReport", markReport.get());
            return "view-personal-mark-report"; // Trả về trang HTML hiển thị báo cáo
        } else {
            model.addAttribute("error", "No mark report found for student ID: " + studentId);
            return "errorView"; // Trả về view lỗi nếu không tìm thấy
        }
    }

    // Xử lý việc cập nhật báo cáo điểm của sinh viên
    @PostMapping("/update-grade")
    public String updateGrade(
            @RequestParam int studentId,
            @RequestParam String studentName,
            @RequestParam String grade,
            @RequestParam String comment,
            Model model) {

        Optional<MarkReport> markReportOpt = markReportService.getMarkReportByStudentId(studentId);

        if (markReportOpt.isPresent()) {
            MarkReport markReport = markReportOpt.get();

            // Cập nhật điểm và nhận xét
            markReport.setGrade(grade);
            markReport.setComment(comment);

            // Lưu lại thông tin đã cập nhật
            markReportService.saveMarkReport(markReport);

            model.addAttribute("markReport", markReport);
            model.addAttribute("message", "Mark report updated successfully.");
            return "view-personal-mark-report"; // Trả về trang cập nhật đã thành công
        } else {
            model.addAttribute("error", "No mark report found for student ID: " + studentId);
            return "errorView"; // Trả về view lỗi nếu không tìm thấy
        }
    }
}
