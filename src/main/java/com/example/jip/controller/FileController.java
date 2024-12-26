package com.example.jip.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    @PostMapping("/export-csv")
    public void exportCsv(@RequestParam("gradesData") String gradesData, HttpServletResponse response) {
        try {
            // Parse JSON data from front-end
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> gradesList = objectMapper.readValue(
                    gradesData,
                    new TypeReference<List<Map<String, String>>>() {}
            );

            // Configure CSV headers
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"class_grades.csv\"");

            // Write CSV data
            try (PrintWriter writer = response.getWriter()) {
                // Write headers
                writer.println("\"Student Name\",\"Teacher Comment\",\"Participation\",\"Daily Exams\",\"Skill\",\"Midterm Test\",\"Final Test\",\"Course Final\"");
                // Write rows
                for (Map<String, String> grade : gradesList) {
                    writer.println("\"" + grade.get("fullname") + "\",\""
                            + grade.getOrDefault("comment", "N/A") + "\",\""
                            + grade.getOrDefault("attitude", "N/A") + "\",\""
                            + grade.getOrDefault("avgExamMark", "N/A") + "\",\""
                            + grade.getOrDefault("skill", "N/A") + "\",\""
                            + grade.getOrDefault("middleExam", "N/A") + "\",\""
                            + grade.getOrDefault("finalExam", "N/A") + "\",\""
                            + grade.getOrDefault("finalMark", "N/A") + "\"");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
