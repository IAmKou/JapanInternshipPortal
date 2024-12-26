package com.example.jip.services;
import com.example.jip.configuration.ConfigProperties;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
@Service
public class CSVExportService {

    @Autowired
    private ConfigProperties configProperties;

    // Hàm trích xuất dữ liệu từ HTML và ghi vào file CSV
    public String extractDataFromHTMLAndGenerateCSV() {
        // Đọc HTML từ file hoặc từ URL (ở đây giả sử bạn lấy dữ liệu từ file)
        String filePath = configProperties.getHtmlPath() + "/input.html";  // Đọc đường dẫn từ ConfigProperties

        try {
            // Đọc trang HTML
            Document doc = Jsoup.parse(new java.io.File(filePath), "UTF-8");

            // Khởi tạo danh sách để chứa dữ liệu
            List<String[]> data = new ArrayList<>();

            // Dữ liệu header
            data.add(new String[] {
                    "Learning Program", "Name", "Teacher Commentation", "Participation",
                    "Daily Exams", "Midterm Test", "Final Exam Test", "Course Final"
            });

            // Trích xuất dữ liệu từ các thẻ HTML
            String learningProgram = doc.select("td:contains(Learning Program)").first().text().split(":")[1].trim();
            String name = doc.select("td:contains(Name)").first().text().split(":")[1].trim();
            String teacherCommentation = doc.select("td:contains(Teacher's Commentation)").first().text().split(":")[1].trim();

            // Trích xuất điểm từ bảng điểm
            String participation = doc.select("td:contains(Attendance Rate)").first().parent().select("td").last().text().trim();
            String dailyExams = doc.select("td:contains(Avg Exams Mark)").first().parent().select("td").last().text().trim();
            String midtermTest = doc.select("td:contains(Mid-term Exam)").first().parent().select("td").last().text().trim();
            String finalExamTest = doc.select("td:contains(End-term Exam)").first().parent().select("td").last().text().trim();
            String courseFinal = doc.select("td:contains(Course Total)").first().parent().select("td").last().text().trim();

            // Thêm dữ liệu vào danh sách
            data.add(new String[] {
                    learningProgram, name, teacherCommentation, participation,
                    dailyExams, midtermTest, finalExamTest, courseFinal
            });

            // Ghi vào file CSV
            String csvFilePath = configProperties.getCsvPath() + "/output.csv";  // Đọc đường dẫn từ ConfigProperties
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(data);
            }

            return csvFilePath; // Trả về đường dẫn đến file CSV

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
