package com.example.jip.controller;


import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.request.markReport.MarkReportUpdateRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.dto.response.markReportExam.MarkReportExamResponse;
import com.example.jip.entity.MarkReport;
import com.example.jip.entity.MarkReportExam;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.MarkReportRepository;
import com.example.jip.services.MarkReportServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.PrintWriter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/mark-report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkReportController {

    @Autowired
    private ListRepository listRepository;

    MarkReportServices markReportServices;
    @PostMapping("/import")
    public ResponseEntity<String> importMarkReports(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        try {
            List<MarkReportImportRequest> markReports = markReportServices.parseExcel(file);
            log.info("markReports: {}", markReports);
            markReportServices.importMarkReports(markReports);
            return ResponseEntity.ok("File imported successfully.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import file: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<MarkReportResponse>> getMarkReport(@RequestParam("classId") int classId) {
        List<MarkReportResponse> responses = markReportServices.getListMarkReport(classId);
        log.info("Mark reports: {}", responses);
        if (responses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/detail")
    public ResponseEntity<MarkReportResponse> getMarkRpById(@RequestParam("markRpId") int markRpId) {
        log.info("Received markRpId: " + markRpId);
        MarkReportResponse response = markReportServices.getMarkReportById(markRpId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/listMRE")
    public ResponseEntity<List<MarkReportExamResponse>> getMarkRpExam(@RequestParam("classId") int classId) {
        List<MarkReportExamResponse> responses = markReportServices.getListMarkReportExam(classId);
        log.info("Mark reports: {}", responses);
        if (responses != null) {
            return ResponseEntity.ok(responses);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/personal-mark-rp")
    public ResponseEntity<MarkReportResponse> getMarkRpByStudentId(@RequestParam("studentId") int studentId) {
        log.info("Received studentId: " + studentId);
        MarkReportResponse response = markReportServices.getMarkReportByStudentId(studentId);
        if (response != null)  {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAssignment(@RequestParam("markRpId") int markRp_id,
                                              @ModelAttribute MarkReportUpdateRequest request) {
        try {
            log.info("Received request: " + request);  // Log the incoming request for debugging

            markReportServices.updateMarkRp(markRp_id, request);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful update
        } catch (NoSuchElementException e) {
            log.error("Assignment not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if assignment doesn't exist
        }
    }
}