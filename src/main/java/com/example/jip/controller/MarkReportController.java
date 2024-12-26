package com.example.jip.controller;


import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.entity.MarkReport;
import com.example.jip.services.MarkReportServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/mark-report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkReportController {

    MarkReportServices markReportServices;
    @PostMapping("/import")
    public ResponseEntity<String> importMarkReports(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        try {
            List<MarkReportImportRequest> markReports = markReportServices.parseCsv(file);
            markReportServices.saveMarkReports(markReports);
            return ResponseEntity.ok("File imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import file: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<MarkReportResponse>> getMarkReport() {
        List<MarkReportResponse> responses = markReportServices.getListMarkReport();
        if (responses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(responses);
    }


}