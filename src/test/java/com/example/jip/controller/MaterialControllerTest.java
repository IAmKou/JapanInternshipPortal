package com.example.jip.controller;

import com.example.jip.entity.Material;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.util.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MaterialControllerTest {

    @InjectMocks
    private MaterialController materialController;

    @Mock
    private MaterialRepository materialRepository;


    @Mock
    private FileUploadUtil fileUploadUtil;

    public MaterialControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateMaterial_SuccessWithoutFile() {
        // Mock input data
        int materialId = 1;
        String title = "Updated Material Title";
        String content = "Updated content for the material.";

        // Mock Material
        Material material = new Material();
        material.setId(materialId);
        material.setTitle("Old Material Title");
        material.setContent("Old content.");

        // Mock repository behavior
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));

        // Call the method
        ResponseEntity<String> response = materialController.updateMaterial(materialId, title, content, null, null);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật tài liệu thành công!", response.getBody());

        // Verify that the material was updated
        assertEquals(title, material.getTitle());
        assertEquals(content, material.getContent());

        // Verify interactions
        verify(materialRepository, times(1)).findById(materialId);
        verify(materialRepository, times(1)).save(material);

    }

    @Test
    void updateMaterial_MaterialNotFound() {
        // Mock input data
        int materialId = 1;
        String title = "Updated Material Title";
        String content = "Updated content for the material.";

        // Mock repository behavior
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<String> response = materialController.updateMaterial(materialId, title, content, null, null);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify interactions
        verify(materialRepository, times(1)).findById(materialId);
        verify(materialRepository, never()).save(any(Material.class));

    }

}
