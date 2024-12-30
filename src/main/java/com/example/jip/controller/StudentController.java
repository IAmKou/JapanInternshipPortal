package com.example.jip.controller;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.StudentWithClassDTO;
import com.example.jip.entity.MarkReport;
import com.example.jip.entity.Student;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.MarkReportRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/student")

public class StudentController {
    @Autowired
    StudentServices studentServices;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    ListRepository listRepository;

    @Autowired
    private MarkReportRepository markReportRepository;


    @PostMapping("/save")
    public ResponseEntity<String> saveStudent(
            @RequestParam String fullname,
            @RequestParam String japanname,
            @RequestParam String dob,
            @RequestParam String gender,
            @RequestParam String email,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) MultipartFile img,
            @RequestParam(required = false) MultipartFile passport_img,
            @RequestParam int account_id) {

        try {
            // Parse the date
            LocalDate localDate;
            Date date;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(dob, formatter);
                date = Date.valueOf(localDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Invalid date format. Please use 'yyyy-MM-dd'.");
            }

            // Call the service to create the student
            studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, passport_img, account_id);

            // Return success response
            return ResponseEntity.ok("Student saved successfully!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again.");
        }
    }

    @GetMapping("/get")
    public List<StudentDTO> getTopStudents() {
        return studentRepository.findTopUnassignedStudents();
    }

    @GetMapping("/getAllStudent")
    public List<StudentWithClassDTO> getAllStudents() {
        List<StudentWithClassDTO> students = listRepository.findAllStudentsWithClassInfo();
        System.out.println(students);
        return students;
    }

    @GetMapping("/getAllWithoutClass")
    public List<StudentWithClassDTO> getStudentsWithoutClass() {
        return listRepository.getStudentsWithoutClass();
    }

    @GetMapping("/{classId}/getAll")
    public List<StudentDTO> getStudentByClassId(@PathVariable Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("classId must not be null");
        }
        List<Student> results = listRepository.findStudentsByClassId(classId);
        return results.stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getStudent")
    public StudentDTO getStudentById(@RequestParam("studentId") Integer studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("studentId must not be null");
        }
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return new StudentDTO(student);
    }

    @GetMapping("/{classId}/getAllGrades")
    public List<MarkReportDTO> getAllGrades(@PathVariable Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("classId must not be null");
        }
        List<MarkReportDTO> markReports = listRepository.getStudentsWithMarkReportsByClassId(classId);
        return markReports;
    }

    // Save grades for a class
    @PostMapping("/{classId}/saveGrades")
    public ResponseEntity<String> saveGrades(@PathVariable Integer classId, @RequestBody List<MarkReportDTO> grades) {
        if (classId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("classId must not be null");
        }

        try {
            // Update grades for each student
            updateGrades(grades);
            return ResponseEntity.ok("Grades updated successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update grades: " + e.getMessage());
        }
    }

    // Update grades for each student
    private void updateGrades(List<MarkReportDTO> grades) {
        for (MarkReportDTO grade : grades) {
            // Fetch existing grade report
            MarkReport markReport = markReportRepository.findByStudentId(grade.getStudentId());
            if (markReport == null) {
                throw new IllegalArgumentException("MarkReport not found for studentId: " + grade.getStudentId());
            }

            // Update grade fields
            markReport.setAttitude(grade.getAttitude());
            markReport.setSoftskill(grade.getSoftskill());
            markReport.setSkill(grade.getSkill());
            markReport.setAvg_exam_mark(grade.getAvgExamMark());
            markReport.setMiddle_exam(grade.getMiddleExam());
            markReport.setFinal_exam(grade.getFinalExam());
            markReport.setFinal_mark(grade.getFinalMark());
            markReport.setComment(grade.getComment());

            // Save updated mark report
            markReportRepository.save(markReport);
        }
    }
}
