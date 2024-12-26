package com.example.jip.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@RestController
public class FileController {
    private static final String HTML_FILE_PATH = "src/main/resources/static/input.html";

    @PostMapping("/export-csv")
    public void exportCsv(HttpServletResponse response) {
        try {
            // Đọc file HTML
            File inputFile = new File(HTML_FILE_PATH);
            Document doc = Jsoup.parse(inputFile, "UTF-8");

            // Lấy dữ liệu từ HTML
//            String learningProgram = getDataFromHtml(doc, "Learning Program");
            String name = getDataFromHtml(doc, "Name");
            String teacherComment = getDataFromHtml(doc, "Teacher\\'s Commentation");
            String participation = getValueFromTable(doc, "Attendance Rate");
            String dailyExams = getValueFromTable(doc, "Avg Exams Mark");
            String midtermTest = getValueFromTable(doc, "Mid-term Exam");
            String finalExamTest = getValueFromTable(doc, "End-term Exam");
            String courseFinal = getValueFromTable(doc, "Course Total");

            // Cấu hình header CSV
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"classinfo.csv\"");

            // Ghi dữ liệu vào CSV
            try (PrintWriter writer = response.getWriter()) {
                // Ghi header
                writer.println("\"Name\",\"Teacher Commentation\",\"Participation\",\"Daily Exams\",\"Midterm Test\",\"Final Exam Test\",\"Course Final\"");
                // Ghi dữ liệu
                writer.println("\",\"" + name + "\",\"" + teacherComment + "\",\"" + participation + "\",\"" + dailyExams + "\",\"" + midtermTest + "\",\"" + finalExamTest + "\",\"" + courseFinal + "\"");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Hàm lấy dữ liệu từ HTML theo tiêu đề (cột)
    private String getDataFromHtml(Document doc, String keyword) {
        Element element = doc.selectFirst("td:contains(" + keyword + ")");
        if (element != null) {
            String[] parts = element.text().split(":");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return ""; // Trả về chuỗi rỗng nếu không tìm thấy
    }

    // Hàm lấy giá trị cuối cùng trong bảng (hàng tương ứng)
    private String getValueFromTable(Document doc, String keyword) {
        Element element = doc.selectFirst("td:contains(" + keyword + ")");
        if (element != null) {
            Element parentRow = element.parent(); // Lấy hàng chứa keyword
            if (parentRow != null) {
                return parentRow.select("td").last().text().trim(); // Lấy cột cuối cùng
            }
        }
        return ""; // Trả về chuỗi rỗng nếu không tìm thấy
    }
}
