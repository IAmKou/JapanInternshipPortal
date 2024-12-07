package com.example.jip.controller;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.entity.Account;
import com.example.jip.entity.Material;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.CloudinaryService;
import com.example.jip.services.MaterialServices;
import com.example.jip.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    private CloudinaryService cloudinaryService;


    @PostMapping("/create")
    public RedirectView createMaterial(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "imgFile", required = false) MultipartFile imgFile,
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

            String img = cloudinaryService.uploadFileToFolder(imgFile, "Materials/").getUrl();
            materialDTO.setImg(img);


            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacher.getId());
            materialDTO.setTeacher(teacherDTO);

            Material savedMaterial = materialServices.createMaterial(materialDTO);

            redirectAttributes.addFlashAttribute("success", "Material '" + savedMaterial.getTitle() + "' created successfully!");

            // Redirect to View-material-details.html with the new material ID
            return new RedirectView("/View-material-details.html?id=" + savedMaterial.getId());
        }  catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create material: " + e.getMessage());
            return new RedirectView("/materials/create");
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
                String folderName = sanitizeFolderName("material/" + title); // Tạo folder cho ảnh
                FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);  // Kiểm tra ảnh hợp lệ
                cloudinaryService.uploadFileToFolder(file, folderName);  // Upload ảnh lên Cloudinary

                // Cập nhật lại thông tin ảnh trong material
                material.setImg(folderName);  // Lưu lại URL ảnh hoặc folder path mới

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaterial(@PathVariable("id") int materialId) {
        Optional<Material> materialOptional = materialRepository.findById(materialId);

        if (!materialOptional.isPresent()) {
            return ResponseEntity.notFound().build();  // Trả về lỗi nếu không tìm thấy tài liệu
        }

        materialRepository.deleteById(materialId);  // Xóa tài liệu khỏi database
        return ResponseEntity.ok("Xóa tài liệu thành công!");  // Trả về phản hồi thành công
    }
    private void uploadFilesToFolder(MultipartFile[] files, String folderName) {
        Set<String> uploadedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty() && uploadedFiles.add(file.getOriginalFilename())) {
                try {
                    FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                    cloudinaryService.uploadFileToFolder(file, folderName);
                } catch (Exception e) {
                    throw new RuntimeException("Error uploading file: " + file.getOriginalFilename(), e);
                }
            }
        }
    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }


}
