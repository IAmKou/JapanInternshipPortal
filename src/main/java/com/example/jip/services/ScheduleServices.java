package com.example.jip.services;

import com.example.jip.dto.ClassScheduleDTO;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ScheduleRepository;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServices {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository;


    public List<ClassScheduleDTO> getUniqueClassSchedule() {
        return scheduleRepository.findUniqueClassSchedule();
    }


    public class ScheduleValidationException extends RuntimeException {
        public ScheduleValidationException(String message) {
            super(message);
        }
    }


}


