package com.example.jip.controller;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.StudentWithClassDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/save")
    public RedirectView saveStudent(
            @RequestParam String fullname,
            @RequestParam String japanname,
            @RequestParam String dob,
            @RequestParam String gender,
            @RequestParam String email,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) MultipartFile img,
            @RequestParam(required = false) MultipartFile passport_img,
            @RequestParam int account_id,
            RedirectAttributes redirectAttributes) {

        try {
            // Parse the date
            LocalDate localDate;
            Date date;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(dob, formatter);
                date = Date.valueOf(localDate);
            } catch (DateTimeParseException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format. Use yyyy-MM-dd.");
                return new RedirectView("/account-settings.html");
            }

            // Call the service to create the student
            studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, passport_img, account_id);
            redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");

        } catch (IllegalArgumentException e) {
            // Handle custom errors from the service
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
            return new RedirectView("/create-account-student.html");
        } catch (Exception e) {
            // Handle any unexpected errors
            redirectAttributes.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return new RedirectView("/create-account-student.html");
        }

        // Redirect back to the account settings page
        return new RedirectView("/account-settings.html");
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

    @GetMapping("/getAllWithoutClass")
    public List<StudentWithClassDTO> getStudentsWithCompleteMarkReport() {
        return listRepository.getStudentsWithCompleteMarkReport();
    }
}
