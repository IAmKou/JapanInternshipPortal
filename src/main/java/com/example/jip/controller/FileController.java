package com.example.jip.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@RestController
public class FileController {

    @Value("${application.file-paths.html-path}")
    private String htmlFilePath;  // Đường dẫn tới file HTML (thêm cấu hình trong application.yml)

    @PostMapping
    @ResponseBody
    public void exportCsv(HttpServletResponse response) throws IOException {
        // Đọc file HTML từ đường dẫn
        Document doc = Jsoup.parse(new java.io.File(htmlFilePath), "UTF-8");

        // Lấy các dữ liệu từ HTML
        String learningProgram = doc.select("td:contains(Learning Program)").first().text().split(":")[1].trim();
        String name = doc.select("td:contains(Name)").first().text().split(":")[1].trim();
        String teacherComment = doc.select("td:contains(Teacher's Commentation)").first().text().split(":")[1].trim();
        String participation = doc.select("td:contains(Attendance Rate)").first().parent().select("td").last().text().trim();
        String dailyExams = doc.select("td:contains(Avg Exams Mark)").first().parent().select("td").last().text().trim();
        String midtermTest = doc.select("td:contains(Mid-term Exam)").first().parent().select("td").last().text().trim();
        String finalExamTest = doc.select("td:contains(End-term Exam)").first().parent().select("td").last().text().trim();
        String courseFinal = doc.select("td:contains(Course Total)").first().parent().select("td").last().text().trim();

        // Cấu hình header CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
        PrintWriter writer = response.getWriter();

        // Ghi header vào CSV
        writer.println("\"Learning Program\",\"Name\",\"Teacher Commentation\",\"Participation\",\"Daily Exams\",\"Midterm Test\",\"Final Exam Test\",\"Course Final\"");

        // Ghi dữ liệu vào CSV
        writer.println("\"" + learningProgram + "\",\"" + name + "\",\"" + teacherComment + "\",\"" + participation + "\",\"" + dailyExams + "\",\"" + midtermTest + "\",\"" + finalExamTest + "\",\"" + courseFinal + "\"");

        // Đóng writer
        writer.flush();
    }
}
