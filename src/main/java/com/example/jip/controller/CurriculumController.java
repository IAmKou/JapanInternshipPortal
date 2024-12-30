package com.example.jip.controller;

import com.example.jip.dto.CurriculumRequest;
import com.example.jip.entity.Curriculum;
import com.example.jip.entity.CurriculumInformation;
import com.example.jip.services.CurriculumInformationService;
import com.example.jip.services.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    @Autowired
    private CurriculumInformationService curriculumInformationService; // Thêm service cho CurriculumInformation

    @PostMapping("/create")
    public ResponseEntity<Object> createCurriculum(@RequestBody CurriculumRequest request) {
        try {
            // Tạo curriculum
            Curriculum curriculum = new Curriculum(request.getSubject(), request.getTotalSlot(), request.getTotalTime());
            Curriculum savedCurriculum = curriculumService.save(curriculum);

            // Tạo CurriculumInformation
            CurriculumInformation curriculumInformation = new CurriculumInformation(savedCurriculum, request.getDescription());
            curriculumInformationService.save(curriculumInformation);  // Lưu vào database

            // Return response
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Curriculum created successfully!",
                    "curriculumId", savedCurriculum.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error creating curriculum: " + e.getMessage()
            ));
        }
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllCurriculums() {
        try {
            List<Map<String, Object>> curriculums = curriculumService.getAllCurriculums();

            if (!curriculums.isEmpty()) {
                return ResponseEntity.ok(curriculums);  // Trả về danh sách curriculum đã có mô tả
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", "no_content",
                        "message", "No curricula found"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error retrieving curricula: " + e.getMessage()
            ));
        }
    }

}