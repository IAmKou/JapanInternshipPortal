package com.example.jip.services;

import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassServices {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ListRepository listRepository;


}
