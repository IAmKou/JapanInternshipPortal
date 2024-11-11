package com.example.jip.controller;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.MaterialServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    // Inject các repository
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MaterialServices materialServices;


    @PostMapping("/create")
    public ResponseEntity<String> createMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("img") String img,  // Truyền img dưới dạng String
            @RequestParam int teacher_id
    ) {
        try {
            LocalDate localDate = LocalDate.now();
            java.sql.Date created_Date = Date.valueOf(localDate);

            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setCreated_date(created_Date);
            materialDTO.setTitle(title);
            materialDTO.setContent(content);
            materialDTO.setImg(img);  // Lưu trực tiếp img dưới dạng String

            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacher_id);
            materialDTO.setTeacher(teacherDTO);

            Material savedMaterial = materialServices.createMaterial(materialDTO);

            return ResponseEntity.ok("Material '" + savedMaterial.getTitle() + "' created with ID: " + savedMaterial.getId());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create material: " + e.getMessage());
        }
    }
}