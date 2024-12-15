package com.example.jip.controller;

import com.example.jip.dto.ReportDTO;
import com.example.jip.repository.ReportRepository;
import com.example.jip.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportServices reportServices;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @PostMapping("/create")
    public ResponseEntity<String> createReport(@RequestParam String title, @RequestParam String content, @RequestParam int uid) {
        try {
            reportServices.createReport(title, content, uid);
            return ResponseEntity.ok("Report submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create report: " + e.getMessage());
        }
    }


    @GetMapping("/get")
    public List<ReportDTO> reportList(){
        return reportRepository.findAll().stream()
                .map(ReportDTO::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/counts")
    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("students", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Student", Long.class));
        counts.put("teachers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Teacher", Long.class));
        counts.put("managers", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Manager", Long.class));
        return counts;
    }
}
