package com.example.jip.controller;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.entity.MarkReport;
import com.example.jip.services.MarkReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class MarkReportController {
    @Autowired
    private MarkReportServices markReportService;

    @GetMapping("/mark-report")
    public String viewMarkReport(@RequestParam int studentId, Model model) {
        Optional<MarkReport> markReport = markReportService.getMarkReportByStudentId(studentId);
        if (markReport.isPresent()) {
            model.addAttribute("markReport", markReport.get());
            return "markReportView"; // Trả về tên view (HTML)
        } else {
            model.addAttribute("error", "No mark report found for student ID: " + studentId);
            return "errorView"; // Trả về view lỗi nếu không tìm thấy
        }
    }

}
