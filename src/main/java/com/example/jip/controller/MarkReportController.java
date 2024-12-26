package com.example.jip.controller;


import com.example.jip.entity.MarkReport;
import com.example.jip.services.MarkReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.util.Optional;

@RestController
@RequestMapping("/markreport")
public class MarkReportController {

    @Autowired
    private MarkReportServices markReportService;

    // Hiển thị báo cáo điểm của sinh viên theo ID
    @GetMapping("/view/{studentId}")
    public String viewMarkReport(@PathVariable int studentId, Model model) {
        Optional<MarkReport> markReportOpt = markReportService.getMarkReportByStudentId(studentId);

        // Kiểm tra xem báo cáo có tồn tại không
        if (markReportOpt.isPresent()) {
            MarkReport markReport = markReportOpt.get();

            // Chuyển dữ liệu vào model để hiển thị trong view
            model.addAttribute("markReport", markReport);

            // Trả về tên view (sử dụng tên file HTML mà bạn muốn hiển thị)
            return "view-personal-mark-report";  // Tên view (file .html)
        } else {
            // Nếu không tìm thấy báo cáo, trả về trang lỗi
            model.addAttribute("error", "No mark report found for student ID: " + studentId);
            return "errorView";  // Tên trang lỗi
        }
    }


    // Xử lý việc cập nhật báo cáo điểm của sinh viên
//    @PutMapping("/update/{id}")
//    public RedirectView updateGrade(
//            @RequestParam int studentId,
//            @RequestParam String studentName,
//            @RequestParam BigDecimal attendant_rate,
//            @RequestParam BigDecimal avg_assignment_mark,
//            @RequestParam BigDecimal avg_exam_mark,
//            @RequestParam BigDecimal reading_mark,
//            @RequestParam BigDecimal listening_mark,
//            @RequestParam BigDecimal speaking_mark,
//            @RequestParam String comment) {
//
//        Optional<MarkReport> markReportOpt = markReportService.getMarkReportByStudentId(studentId);
//
//        if (markReportOpt.isPresent()) {
//            MarkReport markReport = markReportOpt.get();
//
//            // Cập nhật điểm và nhận xét
//            markReport.setAttendant_rate(attendant_rate);
//            markReport.setAvg_assignment_mark(avg_assignment_mark);
//            markReport.setAvg_exam_mark(avg_exam_mark);
//            markReport.setReading_mark(reading_mark);
//            markReport.setListening_mark(listening_mark);
//            markReport.setSpeaking_mark(speaking_mark);
//            markReport.setComment(comment);
//
//            // Lưu lại thông tin đã cập nhật
//            markReportService.saveMarkReport(markReport);
//
//            // Chuyển hướng đến trang báo cáo cá nhân đã cập nhật
//            return new RedirectView("/view-personal-mark-report.html?studentId=" + studentId);
//        } else {
//            // Nếu không tìm thấy báo cáo, chuyển hướng đến trang lỗi
//            return new RedirectView("/errorView.html?error=No mark report found for student ID: " + studentId);
//        }
//    }
}