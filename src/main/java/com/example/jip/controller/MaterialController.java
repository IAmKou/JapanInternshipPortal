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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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
    public RedirectView createMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false) MultipartFile img,
            @RequestParam("teacher_id") int teacherId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(teacherId);
            if (!teacherOptional.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Teacher with ID " + teacherId + " not found.");
                return new RedirectView("/materials/create");
            }

            Teacher teacher = teacherOptional.get();
            Date createdDate = new Date();  // Current date

            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setCreated_date(createdDate);
            materialDTO.setTitle(title);
            materialDTO.setContent(content);
            materialDTO.setImg(img != null && !img.isEmpty() ? materialServices.saveImage(img) : null);

            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacher.getId());
            materialDTO.setTeacher(teacherDTO);

            Material savedMaterial = materialServices.createMaterial(materialDTO);

            redirectAttributes.addFlashAttribute("success", "Material '" + savedMaterial.getTitle() + "' created successfully!");

            // Redirect to View-material-details.html with the new material ID
            return new RedirectView("/View-material-details.html?id=" + savedMaterial.getId());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            return new RedirectView("/materials/create");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create material: " + e.getMessage());
            return new RedirectView("/materials/create");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = materialRepository.findAll();
        return ResponseEntity.ok(materials);
    }

    // API lấy chi tiết tài liệu theo ID
    @GetMapping("/details/{id}")
    // Update the method signature
    public ResponseEntity<MaterialDTO> getMaterialDetails(@PathVariable("id") int materialId) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);

        if (!materialOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Material material = materialOptional.get();

        // Tạo DTO và gán dữ liệu
        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setId(material.getId());
        materialDTO.setTitle(material.getTitle());
        materialDTO.setContent(material.getContent());
        materialDTO.setImg(material.getImg());
        if (material.getCreated_date() != null) {
            // Nếu material có ngày, trực tiếp gán ngày vào DTO mà không cần định dạng thành String
            materialDTO.setCreated_date(material.getCreated_date());
        }
        // Gán thông tin teacher


        if (material.getTeacher() != null) {
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(material.getTeacher().getId());
            teacherDTO.setFullname(material.getTeacher().getFullname());
            materialDTO.setTeacher(teacherDTO);
        } else {
            // Nếu không có teacher, bạn có thể để nó là null hoặc xử lý sao cho hợp lý
            materialDTO.setTeacher(null);
        }

        return ResponseEntity.ok(materialDTO);

    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateMaterial(
            @PathVariable("id") int materialId,  // Lấy ID tài liệu từ URL
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false) MultipartFile img,  // File ảnh (nếu có)
            RedirectAttributes redirectAttributes
    ) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);

        if (!materialOptional.isPresent()) {
            return ResponseEntity.notFound().build();  // Nếu không tìm thấy tài liệu với ID này
        }

        Material material = materialOptional.get();

        // Cập nhật các thông tin
        material.setTitle(title);
        material.setContent(content);

        // Nếu có file mới, lưu file và cập nhật
        if (img != null && !img.isEmpty()) {
            try {
                String savedImagePath = materialServices.saveImage(img);  // Lưu ảnh và nhận đường dẫn
                material.setImg(savedImagePath);  // Cập nhật ảnh mới
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Lỗi khi tải ảnh lên: " + e.getMessage());
            }
        }

        // Lưu lại tài liệu đã cập nhật vào cơ sở dữ liệu
        materialRepository.save(material);

        // Trả về kết quả thành công
        return ResponseEntity.ok("Cập nhật tài liệu thành công!");
        }
}
