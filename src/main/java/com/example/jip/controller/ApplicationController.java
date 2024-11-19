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
    public RedirectView createApplication(
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
                    redirectAttributes.addFlashAttribute("error", "Teacher với ID " + teacherId + " không tồn tại.");
                    return new RedirectView("/application/create");
                }
            } else if (studentId != null) {  // Nếu không có teacher_id thì lấy student_id
                Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setId(student.getId());
                    applicationDTO.setStudent(studentDTO);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Student với ID " + studentId + " không tồn tại.");
                    return new RedirectView("/application/create");
                }
            }

            // Nếu cả hai ID đều không có, báo lỗi
            if (teacherId == null && studentId == null) {
                redirectAttributes.addFlashAttribute("error", "Cả Teacher ID và Student ID đều không được cung cấp.");
                return new RedirectView("/application/create");
            }

            applicationDTO.setStatus(ApplicationDTO.status.Pending);
            applicationDTO.setReply("");
            applicationDTO.setReplied_date(null);

            // Lưu Application
            Application savedApplication = applicationServices.createApplication(applicationDTO);

            redirectAttributes.addFlashAttribute("success", "Application '" + applicationDTO.getCategory() + "' created successfully!");
            return new RedirectView("/View-my-application.html?id=" + savedApplication.getId());

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            return new RedirectView("/application/create");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create application: " + e.getMessage());
            return new RedirectView("/application/create");
        }
    }
}
