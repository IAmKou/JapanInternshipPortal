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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
}
