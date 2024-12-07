package com.example.jip.controller;

import com.example.jip.dto.ApplicationDTO;
import com.example.jip.entity.Application;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ApplicationRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ApplicationServices;
import com.example.jip.services.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ApplicationServices applicationServices;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
    }

    @Test
    public void testCreateApplicationWithTeacherId() throws Exception {
        Integer teacherId = 1;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        // Mô phỏng hành vi trả về của teacherRepository và applicationServices
        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(Optional.of(teacher));

        // Giả định ứng dụng tạo thành công và trả về Application hợp lệ với ID = 1
        Application savedApplication = new Application();
        savedApplication.setId(1);  // Set the ID to 1
        when(applicationServices.createApplication(Mockito.any(ApplicationDTO.class)))
                .thenReturn(savedApplication);

        // Thực hiện yêu cầu HTTP
        mockMvc.perform(post("/application/create")
                        .param("name", "Application Name")
                        .param("category", "Category")
                        .param("content", "Content")
                        .param("teacher_id", String.valueOf(teacherId))  // Đảm bảo teacher_id được truyền đúng
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())  // Kiểm tra trạng thái chuyển hướng
                .andExpect(redirectedUrl("/View-my-application.html"))  // Kiểm tra URL chuyển hướng
                .andExpect(flash().attributeExists("message"))  // Kiểm tra thông báo thành công trong FlashAttributes
                .andExpect(flash().attribute("message", "Application 'Category' created successfully with ID: 1"));  // Kiểm tra thông báo thành công
    }



    @Test
    public void testCreateApplicationWithStudentId() throws Exception {
        Integer studentId = 2;
        Student student = new Student();
        student.setId(studentId);

        // Mô phỏng repository trả về dữ liệu đúng
        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.of(student));

        // Mô phỏng createApplication trả về Application với ID hợp lệ
        Application savedApplication = new Application();
        savedApplication.setId(1);  // Set a mock ID for testing
        when(applicationServices.createApplication(Mockito.any(ApplicationDTO.class))).thenReturn(savedApplication);

        // Thực hiện yêu cầu (không có imgFile, chỉ có student_id)
        mockMvc.perform(post("/application/create")  // Chú ý thay đổi đường dẫn ở đây
                        .param("name", "Application Name")
                        .param("category", "Category")
                        .param("content", "Content")
                        .param("student_id", String.valueOf(studentId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())  // Kiểm tra status chuyển hướng
                .andExpect(MockMvcResultMatchers.redirectedUrl("/View-my-application.html"))  // Kiểm tra URL chuyển hướng
                .andExpect(flash().attributeExists("message"))  // Kiểm tra thông báo thành công trong FlashAttributes
                .andExpect(flash().attribute("message", "Application 'Category' created successfully with ID: 1"));  // Kiểm tra thông báo thành công
    }




    @Test
    public void testGetAllApplicationsWithTeacherId() throws Exception {
        Integer teacherId = 1;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        Application application = new Application();
        application.setId(1);
        application.setName("Application 1");

        when(teacherRepository.findByAccount_id(teacherId)).thenReturn(java.util.Optional.of(teacher));
        when(applicationRepository.findByTeacher_Id(teacher.getId())).thenReturn(java.util.Collections.singletonList(application));

        mockMvc.perform(get("/application/list")
                        .param("teacher_id", String.valueOf(teacherId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Application 1"));
    }

    @Test
    public void testGetAllApplicationsWithStudentId() throws Exception {
        Integer studentId = 2;
        Student student = new Student();
        student.setId(studentId);

        Application application = new Application();
        application.setId(2);
        application.setName("Application 2");

        when(studentRepository.findByAccount_id(studentId)).thenReturn(java.util.Optional.of(student));
        when(applicationRepository.findByStudent_Id(student.getId())).thenReturn(java.util.Collections.singletonList(application));

        mockMvc.perform(get("/application/list")
                        .param("student_id", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Application 2"));
    }

    @Test
    public void testGetApplicationDetails() throws Exception {
        Integer applicationId = 1;
        Application application = new Application();
        application.setId(applicationId);
        application.setName("Application Details");

        when(applicationRepository.findById(applicationId)).thenReturn(java.util.Optional.of(application));

        mockMvc.perform(get("/application/details/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId))
                .andExpect(jsonPath("$.name").value("Application Details"));
    }

    @Test
    public void testUpdateApplication() throws Exception {
        Integer applicationId = 1;
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Approved");
        payload.put("reply", "Application approved");

        // Create mock student and application
        Student student = new Student();
        student.setId(2); // Ensure student ID is set
        student.setMark(true); // Set the necessary mark value

        Application application = new Application();
        application.setId(applicationId);
        application.setStudent(student); // Link student to application

        Mockito.when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        Mockito.when(studentRepository.findById(2)).thenReturn(Optional.of(student));  // Mock student lookup

        mockMvc.perform(put("/application/update/{id}", applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Application reply success"));
    }


}