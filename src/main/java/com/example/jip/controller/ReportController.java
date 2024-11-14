package com.example.jip.controller;

import com.example.jip.dto.ReportDTO;
import com.example.jip.entity.Report;
import com.example.jip.repository.ReportRepository;
import com.example.jip.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportServices reportServices;


    @PostMapping("/create")
    public String createReport(@RequestParam String title, @RequestParam String content, @RequestParam int reporter_id) {
        reportServices.createReport(title, content, reporter_id);
        return "success";
    }

    @GetMapping("/get")
    public List<ReportDTO> reportList(){
        return reportRepository.findAll().stream()
                .map(ReportDTO::new)
                .collect(Collectors.toList());
    }
}
