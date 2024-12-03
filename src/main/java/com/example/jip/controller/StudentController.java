package com.example.jip.controller;

import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ListRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.StudentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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
            @RequestParam String fullname
            , @RequestParam String japanname
            , @RequestParam String dob
            , @RequestParam String gender
            , @RequestParam String email
            , @RequestParam String phoneNumber
            , @RequestParam(required = false) String img
            , @RequestParam(required = false) String passport_img
            , @RequestParam int account_id
            , RedirectAttributes redirectAttributes) {

        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            localDate = LocalDate.parse(dob, formatter);
        } catch (DateTimeParseException e) {
            RedirectView redirectView = new RedirectView("/error");
            redirectView.addStaticAttribute("message", "Invalid date format. Use yyyy-MM-dd.");
            return redirectView;
        }


        Date date = Date.valueOf(localDate);
        studentServices.createStudent(fullname, japanname, date, gender, phoneNumber, email, img, passport_img, account_id);
        redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
        return new RedirectView("/account-settings.html");
    }

    @GetMapping("/get")
    public List<StudentDTO> getTopStudents() {
        return studentRepository.findTop30UnassignedStudents();
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


}
