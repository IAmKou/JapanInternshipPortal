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
            @RequestParam(value = "img", required = false) MultipartFile img,  // img không bắt buộc
            @RequestParam("teacher_id") String teacher_id  // Chuyển thành String để kiểm tra trước khi chuyển đổi
    ) {
        try {
            System.out.println("Received Teacher ID: " + teacher_id);
            // Kiểm tra xem teacher_id có hợp lệ không
            if (teacher_id == null || teacher_id.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Teacher ID is required.");
            }

            int teacherId;
            try {
                teacherId = Integer.parseInt(teacher_id);  // Chuyển đổi từ String sang int
            } catch (NumberFormatException e) {
                return ResponseEntity.status(400).body("Teacher ID must be a valid number.");
            }

            // Kiểm tra xem teacher có tồn tại trong cơ sở dữ liệu không
            Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
            if (!teacherOptional.isPresent()) {
                return ResponseEntity.status(400).body("Teacher with ID " + teacherId + " not found.");
            }

            // Nếu có ảnh, lưu ảnh và lấy tên ảnh
            String imgFileName = null;
            if (img != null && !img.isEmpty()) {
                imgFileName = materialServices.saveImage(img);  // Nếu có ảnh, gọi phương thức lưu ảnh
            }

            // Tạo ngày tạo tự động
            LocalDate localDate = LocalDate.now();
            java.sql.Date created_Date = Date.valueOf(localDate);

            // Tạo MaterialDTO và gán các giá trị
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setCreated_date(created_Date);
            materialDTO.setTitle(title);
            materialDTO.setContent(content);
            materialDTO.setImg(imgFileName);  // Lưu tên file vào DTO, nếu có

            // Tạo TeacherDTO và thiết lập ID của giáo viên
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacherId);
            materialDTO.setTeacher(teacherDTO);

            // Gọi service để lưu material mới
            Material savedMaterial = materialServices.createMaterial(materialDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.ok("Material '" + savedMaterial.getTitle() + "' created with ID: " + savedMaterial.getId());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create material: " + e.getMessage());
        }
    }
}