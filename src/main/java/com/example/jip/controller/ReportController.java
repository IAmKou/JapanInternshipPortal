package com.example.jip.controller;

import com.example.jip.entity.Report;
import com.example.jip.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get")
    public List<Report> reportList(){
        List<Report> reportList = reportServices.getAllReport();
        return reportList;
    }
}
