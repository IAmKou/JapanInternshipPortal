package com.example.jip.controller;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.*;
import com.example.jip.repository.*;
import com.example.jip.services.MaterialServices;
import com.example.jip.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    // Inject các repository
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MaterialServices materialServices;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private PersonalMaterialRepository personalMaterialRepository;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/create")
    public RedirectView createMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "imgFile", required = false) MultipartFile imgFile,
            @RequestParam("teacher_id") int teacherId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Kiểm tra nếu title đã tồn tại trong cơ sở dữ liệu
            Optional<Material> existingMaterial = materialRepository.findByTitle(title);
            if (existingMaterial.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Material with title '" + title + "' already exists.");
                return new RedirectView("/materials/create");  // Chuyển hướng lại nếu title đã tồn tại
            }

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

            // Upload image and set it to materialDTO
            if (imgFile != null) {
                String fileName = imgFile.getOriginalFilename(); // Lấy tên file gốc
                String folderName = "Materials/" + materialDTO.getTitle(); // Đường dẫn thư mục tổng và con

                // Tải file lên S3
                String img = s3Service.uploadFile(imgFile, folderName, fileName);

                // Cập nhật URL ảnh vào materialDTO
                materialDTO.setImg(img);
            }


            // Set teacher to materialDTO
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacher.getId());
            materialDTO.setTeacher(teacherDTO);

            // Save material
            Material savedMaterial = materialServices.createMaterial(materialDTO);

            // Redirect with success message
            redirectAttributes.addFlashAttribute("success", "Material '" + savedMaterial.getTitle() + "' created successfully!");

            // Redirect to View-material-details.html with the new material ID
            return new RedirectView("/View-material-details.html?id=" + savedMaterial.getId());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create material: " + e.getMessage());
            return new RedirectView("/materials/create");  // Chuyển hướng nếu có lỗi
        }
    }

    // API lấy chi tiết tài liệu theo ID
    @GetMapping("/details/{id}")
    // Update the method signature
    public ResponseEntity<MaterialDTO> getMaterialDetails(@PathVariable("id") Integer materialId,@RequestParam("accountId") Integer accountId) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);
        if (materialOptional.isPresent()) {
            Material material = materialOptional.get();
            MaterialDTO materialDTO = new MaterialDTO();

            // Gán dữ liệu từ Material sang MaterialDTO
            materialDTO.setId(material.getId());
            materialDTO.setTitle(material.getTitle());
            materialDTO.setContent(material.getContent());
            materialDTO.setCreated_date(material.getCreated_date()); // Gán created_date
            materialDTO.setImg(material.getImg());
            Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(accountId);
            Integer teacherId = null;

            // Nếu tìm thấy teacher, lấy teacher_id
            if (teacherOptional.isPresent()) {
                teacherId = teacherOptional.get().getId();
            }

            // Gán thông tin teacher (nếu có)
            if (material.getTeacher() != null) {
                TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setId(material.getTeacher().getId());
                teacherDTO.setFullname(material.getTeacher().getFullname());
                materialDTO.setTeacher(teacherDTO);
            } else {
                materialDTO.setTeacher(null);
            }
            materialDTO.setTeacherId(teacherId);

            return ResponseEntity.ok(materialDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateMaterial(
            @PathVariable("id") int materialId,  // Lấy ID tài liệu từ URL
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,  // File ảnh (nếu có)
            RedirectAttributes redirectAttributes
    ) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);
        if (!materialOptional.isPresent()) {
            return ResponseEntity.notFound().build();  // Nếu không tìm thấy tài liệu với ID này
        }

        Material material = materialOptional.get();

        // Cập nhật các thông tin trong Material
        material.setTitle(title);
        material.setContent(content);

        // Nếu có ảnh mới, xử lý và lưu ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename(); // Lấy tên file gốc
                String folderName = "Materials/" + sanitizeFolderName(material.getTitle()); // Đặt folder theo title mới

                // Upload ảnh lên S3 hoặc dịch vụ tương tự
                String img = s3Service.uploadFile(file, folderName, fileName);

                // Cập nhật lại URL ảnh mới
                material.setImg(img);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
            }
        }

        // Lưu lại tài liệu đã cập nhật vào cơ sở dữ liệu
        materialRepository.save(material);

        return ResponseEntity.ok("Cập nhật tài liệu thành công!");
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMaterials(@RequestParam("accountId") Integer accountId) {
        // Tìm Teacher từ accountId
        Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(accountId);
        Integer teacherId = null;

        // Nếu tìm thấy teacher, lấy teacher_id
        if (teacherOptional.isPresent()) {
            teacherId = teacherOptional.get().getId();
        }

        // Lấy tất cả các material
        List<Material> materials = materialRepository.findAll();

        // Chuyển đổi danh sách materials sang DTO
        List<MaterialDTO> materialDTOs = materials.stream().map(material -> {
            MaterialDTO dto = new MaterialDTO();
            dto.setId(material.getId());
            dto.setTitle(material.getTitle());
            dto.setImg(material.getImg());
            dto.setCreated_date(material.getCreated_date());

            if (material.getTeacher() != null) {
                TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setId(material.getTeacher().getId());  // Lấy teacher_id
                dto.setTeacher(teacherDTO);
            }

            return dto;
        }).collect(Collectors.toList());

        // Trả về teacherId cùng với danh sách materials trong một Map
        Map<String, Object> response = new HashMap<>();
        response.put("teacherId", teacherId);
        response.put("materials", materialDTOs);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/list1")
    public ResponseEntity<List<MaterialDTO>> getMaterials() {
        // Lấy tất cả các material
        List<Material> materials = materialRepository.findAll();

        // Chuyển đổi danh sách materials sang DTO
        List<MaterialDTO> materialDTOs = materials.stream().map(material -> {
            MaterialDTO dto = new MaterialDTO();
            dto.setId(material.getId());
            dto.setTitle(material.getTitle());
            dto.setImg(material.getImg());
            dto.setCreated_date(material.getCreated_date());

            if (material.getTeacher() != null) {
                TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setId(material.getTeacher().getId()); // Lấy teacher_id
                dto.setTeacher(teacherDTO);
            }

            return dto;
        }).collect(Collectors.toList());

        // Trả về danh sách DTO
        return ResponseEntity.ok(materialDTOs);
    }
    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaterial(@PathVariable("id") int materialId) {
        // Kiểm tra xem Material có tồn tại không
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material không tồn tại"));

        // Xóa các bản ghi liên quan trong PersonalMaterial
        personalMaterialRepository.deleteByMaterial(material);

        // Xóa Material
        materialRepository.delete(material);

        return ResponseEntity.ok("Xóa tài liệu và các bản ghi liên quan thành công!");
    }


    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }


}
