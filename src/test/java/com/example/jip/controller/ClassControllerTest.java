package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ListRepository;
import com.example.jip.services.ClassServices;
import com.example.jip.services.NotificationServices;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassControllerTest {


    @InjectMocks
    private ClassController classController;

    @Mock
    private ClassServices classServices;

    @Mock
    private NotificationServices notificationServices;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private ListRepository listRepository;

    public ClassControllerTest() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testUpdateClass_Success() {
        // Arrange
        ClassDTO classDTO = new ClassDTO();
        classDTO.setId(1);
        classDTO.setName("Updated Class");

        when(classServices.updateClass(anyInt(), any(ClassDTO.class))).thenReturn(classDTO);

        // Act
        ResponseEntity<ClassDTO> response = classController.updateClass(classDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Class", response.getBody().getName());
        verify(classServices, times(1)).updateClass(anyInt(), any(ClassDTO.class));
    }

    @Test
    void testDeleteClass_Success() {
        // Arrange
        int classId = 1;
        when(classRepository.existsById(classId)).thenReturn(true);

        // Act
        boolean result = classController.deleteClass(classId);

        // Assert
        assertTrue(result);
        verify(listRepository, times(1)).deleteStudentsByClassId(classId);
        verify(classRepository, times(1)).deleteById(classId);
    }

    @Test
    void testDeleteClass_ClassNotFound() {
        // Arrange
        int classId = 999; // Non-existing class ID
        when(classRepository.existsById(classId)).thenReturn(false);

        // Act
        boolean result = classController.deleteClass(classId);

        // Assert
        assertFalse(result);
        verify(listRepository, never()).deleteStudentsByClassId(classId);
        verify(classRepository, never()).deleteById(classId);
    }
}