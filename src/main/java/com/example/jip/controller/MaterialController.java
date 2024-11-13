package com.example.jip.controller;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Account;
import com.example.jip.entity.Material;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.MaterialServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private AccountRepository accountRepository;


    @PostMapping("/create")
    public ResponseEntity<String> createMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false) MultipartFile img,
            @RequestParam("teacher_id") int teacherId  // Lấy teacherId từ request

    ) {
        try {

            // Tìm teacher dựa vào teacherId trong database
            Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(teacherId);
            if (!teacherOptional.isPresent()) {
                return ResponseEntity.status(400).body("Teacher with ID " + teacherId + " not found.");
            }

            Teacher teacher = teacherOptional.get();

            // Các bước tiếp theo để lưu Material
            LocalDate localDate = LocalDate.now();
            java.sql.Date created_Date = Date.valueOf(localDate);

            // Tạo MaterialDTO và gán các giá trị
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setCreated_date(created_Date);
            materialDTO.setTitle(title);
            materialDTO.setContent(content);
            materialDTO.setImg(img != null && !img.isEmpty() ? materialServices.saveImage(img) : null);

            int id = teacherOptional.get().getId();
            // Gán TeacherDTO vào MaterialDTO
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(id);  // Gán teacherId vào DTO
            materialDTO.setTeacher(teacherDTO);

            // Gọi service để lưu material mới
            Material savedMaterial = materialServices.createMaterial(materialDTO);

            return ResponseEntity.ok("Material '" + savedMaterial.getTitle() + "' created with ID: " + savedMaterial.getId());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create material: " + e.getMessage());
        }
    }
}