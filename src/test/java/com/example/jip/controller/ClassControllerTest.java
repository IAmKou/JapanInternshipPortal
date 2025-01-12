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







}