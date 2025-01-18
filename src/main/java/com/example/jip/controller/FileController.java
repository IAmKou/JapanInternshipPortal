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

import java.nio.charset.StandardCharsets;

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
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"class_grades.csv\"");

            // Write CSV data with UTF-8 BOM
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8), true)) {
                // Write BOM for UTF-8
                writer.write('\ufeff');

                // Write headers
                writer.println("\"Student Name\",\"Comment\",\"Avg Exam Mark\",\"Middle Exam\",\"Final Exam\",\"Skill\",\"Script\",\"Presentation\",\"Soft Skill\",\"Assignment\",\"Attendant\",\"Attitude\",\"Course Total\"");

                // Write rows
                for (Map<String, String> grade : gradesList) {
                    writer.println("\"" + handleNull(grade.get("studentName"), "N/A") + "\",\""
                            + handleNull(grade.get("comment"), "none") + "\",\""
                            + handleNull(grade.get("avg_exam_mark"), "0") + "\",\""
                            + handleNull(grade.get("middle_exam"), "0") + "\",\""
                            + handleNull(grade.get("final_exam"), "0") + "\",\""
                            + handleNull(grade.get("skill"), "0") + "\",\""
                            + handleNull(grade.get("scriptPresentation"), "0") + "\",\""
                            + handleNull(grade.get("presentation"), "0") + "\",\""
                            + handleNull(grade.get("softskill"), "0") + "\",\""
                            + handleNull(grade.get("assignment"), "0") + "\",\""
                            + handleNull(grade.get("attendant"), "0") + "\",\""
                            + handleNull(grade.get("attitude"), "0") + "\",\""
                            + handleNull(grade.get("final_mark"), "0") + "\"");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String handleNull(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}