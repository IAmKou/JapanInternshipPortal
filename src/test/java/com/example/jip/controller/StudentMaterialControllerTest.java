package com.example.jip.controller;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.PersonalMaterialDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.PersonalMaterialRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.services.PersonalMaterialServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudentMaterialControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonalMaterialServices personalMaterialServices;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private PersonalMaterialRepository personalMaterialRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentMaterialController studentMaterialController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentMaterialController).build();
    }

    @Test
    public void testAddMaterial() throws Exception {
        // Mock dữ liệu đầu vào
        int studentId = 1;
        int materialId = 2;
        String materialLink = "http://example.com/material";

        Student student = new Student();
        student.setId(studentId);

        Material material = new Material();
        material.setId(materialId);
        material.setTitle("Sample Material");

        PersonalMaterialDTO personalMaterialDTO = new PersonalMaterialDTO();
        personalMaterialDTO.setMaterial_link(materialLink);
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(studentId);
        personalMaterialDTO.setStudent(studentDTO);

        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setTitle(material.getTitle());
        personalMaterialDTO.setMaterial(materialDTO);

        // Tạo một đối tượng PersonalMaterial đã được khởi tạo
        PersonalMaterial savedPersonalMaterial = new PersonalMaterial();
        savedPersonalMaterial.setMaterial_link(materialLink);

        // Mock các phương thức repository và service
        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.of(student));
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(personalMaterialServices.addMaterial(any(PersonalMaterialDTO.class))).thenReturn(savedPersonalMaterial);

        // Thực hiện request và kiểm tra kết quả
        mockMvc.perform(post("/studentMaterials/add")
                        .param("material_link", materialLink)
                        .param("student_id", String.valueOf(studentId))
                        .param("material_id", String.valueOf(materialId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(materialLink));
    }
    @Test
    public void testListPersonalMaterials() throws Exception {
        int studentId = 1;

        Student student = new Student();
        student.setId(studentId);

        PersonalMaterial personalMaterial = new PersonalMaterial();
        personalMaterial.setId(1);
        personalMaterial.setMaterial_link("http://example.com/material");

        Material material = new Material();
        material.setId(2);
        material.setTitle("Sample Material");
        personalMaterial.setMaterial(material);

        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.of(student));
        when(personalMaterialRepository.findByStudent_Id(studentId)).thenReturn(java.util.List.of(personalMaterial));

        mockMvc.perform(get("/studentMaterials/list")
                        .param("student_id", String.valueOf(studentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].material_link").value("http://example.com/material"))
                .andExpect(jsonPath("$[0].material.title").value("Sample Material"));
    }
    @Test
    public void testDeletePersonalMaterial() throws Exception {
        int materialId = 1;

        PersonalMaterial personalMaterial = new PersonalMaterial();
        personalMaterial.setId(materialId);

        when(personalMaterialRepository.findById(materialId)).thenReturn(Optional.of(personalMaterial));

        mockMvc.perform(delete("/studentMaterials/delete/{id}", materialId))
                .andExpect(status().isOk())
                .andExpect(content().string("Material removed successfully."));
    }


    @Test
    void addMaterial_StudentNotFound() {
        // Mock input data
        String materialLink = "http://example.com/material";
        int studentId = 1;
        int materialId = 100;

        // Mock repository behavior
        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<String> response = studentMaterialController.addMaterial(materialLink, studentId, materialId);

        // Assertions
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Student with studentId " + studentId + " not found.", response.getBody());

        // Verify interactions
        verify(studentRepository, times(1)).findByAccount_id(studentId);
        verify(materialRepository, never()).findById(anyInt());
        verify(personalMaterialRepository, never()).findByStudent_IdAndMaterial_link(anyInt(), anyString());
        verify(personalMaterialServices, never()).addMaterial(any(PersonalMaterialDTO.class));
    }

    @Test
    void addMaterial_MaterialNotFound() {
        // Mock input data
        String materialLink = "http://example.com/material";
        int studentId = 1;
        int materialId = 100;

        // Mock student data
        Student student = new Student();
        student.setId(studentId);

        // Mock repository behavior
        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.of(student));
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<String> response = studentMaterialController.addMaterial(materialLink, studentId, materialId);

        // Assertions
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Material with materialId " + materialId + " not found.", response.getBody());

        // Verify interactions
        verify(studentRepository, times(1)).findByAccount_id(studentId);
        verify(materialRepository, times(1)).findById(materialId);
        verify(personalMaterialRepository, never()).findByStudent_IdAndMaterial_link(anyInt(), anyString());
        verify(personalMaterialServices, never()).addMaterial(any(PersonalMaterialDTO.class));
    }

    @Test
    void addMaterial_ConflictMaterialAlreadyExists() {
        // Mock input data
        String materialLink = "http://example.com/material";
        int studentId = 1;
        int materialId = 100;

        // Mock student and material data
        Student student = new Student();
        student.setId(studentId);

        PersonalMaterial existingMaterial = new PersonalMaterial();

        // Mock repository behavior
        when(studentRepository.findByAccount_id(studentId)).thenReturn(Optional.of(student));
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(new Material()));
        when(personalMaterialRepository.findByStudent_IdAndMaterial_link(student.getId(), materialLink))
                .thenReturn(Optional.of(existingMaterial));

        // Call the method
        ResponseEntity<String> response = studentMaterialController.addMaterial(materialLink, studentId, materialId);

        // Assertions
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Material đã tồn tại cho học sinh này.", response.getBody());

        // Verify interactions
        verify(studentRepository, times(1)).findByAccount_id(studentId);
        verify(materialRepository, times(1)).findById(materialId);
        verify(personalMaterialRepository, times(1))
                .findByStudent_IdAndMaterial_link(student.getId(), materialLink);
        verify(personalMaterialServices, never()).addMaterial(any(PersonalMaterialDTO.class));
    }

    @Test
    void addMaterial_InternalServerError() {
        // Mock input data
        String materialLink = "http://example.com/material";
        int studentId = 1;
        int materialId = 100;

        // Mock repository behavior
        when(studentRepository.findByAccount_id(studentId)).thenThrow(new RuntimeException("Database error"));

        // Call the method
        ResponseEntity<String> response = studentMaterialController.addMaterial(materialLink, studentId, materialId);

        // Assertions
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Lỗi khi thêm material: Database error", response.getBody());

        // Verify no interactions with the other repositories
        verify(materialRepository, never()).findById(anyInt());
        verify(personalMaterialRepository, never()).findByStudent_IdAndMaterial_link(anyInt(), anyString());
        verify(personalMaterialServices, never()).addMaterial(any(PersonalMaterialDTO.class));
    }

}
