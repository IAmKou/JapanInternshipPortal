package com.example.jip.controller;

import com.example.jip.repository.ReportRepository;
import com.example.jip.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportServices reportServices;


    @PostMapping("/create")
    public String createReport(@RequestParam String title, @RequestParam String content, @RequestParam int reporter_id) {
        reportServices.createReport(title, content, reporter_id);
        return "success";
    }
}
