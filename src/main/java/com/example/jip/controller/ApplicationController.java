package com.example.jip.controller;

import com.example.jip.dto.ApplicationDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Application;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ApplicationRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ApplicationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationServices applicationServices;

    @PostMapping("/create")
    public ResponseEntity<String> createApplication(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false) MultipartFile img,
            @RequestParam(value = "teacher_id", required = false) Integer teacherId,
            @RequestParam(value = "student_id", required = false) Integer studentId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setCreated_date(new Date());
            applicationDTO.setName(name);
            applicationDTO.setCategory(category);
            applicationDTO.setContent(content);
            applicationDTO.setImg(img != null && !img.isEmpty() ? applicationServices.saveImage(img) : null);

            // Kiểm tra và lấy teacher_id nếu có, nếu không thì lấy student_id
            if (teacherId != null) {
                Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(teacherId);
                if (teacherOptional.isPresent()) {
                    Teacher teacher = teacherOptional.get();
                    TeacherDTO teacherDTO = new TeacherDTO();
                    teacherDTO.setId(teacher.getId());
                    applicationDTO.setTeacher(teacherDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Teacher with ID " + teacherId + " not found.");
                }
            } else if (studentId != null) {  // Nếu không có teacher_id thì lấy student_id
                Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setId(student.getId());
                    applicationDTO.setStudent(studentDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Student with ID " + studentId + " not found.");
                }
            }

            // Nếu cả hai ID đều không có, báo lỗi
            if (teacherId == null && studentId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Both Teacher ID and Student ID must be provided.");
            }

            applicationDTO.setStatus(ApplicationDTO.status.Pending);
            applicationDTO.setReply("");
            applicationDTO.setReplied_date(null);

            // Lưu Application
            Application savedApplication = applicationServices.createApplication(applicationDTO);

            // Trả về ResponseEntity với thông tin ứng dụng vừa tạo
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Application '" + applicationDTO.getCategory() + "' created successfully with ID: " + savedApplication.getId());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create application: " + e.getMessage());
        }
    }
}
