package com.example.jip.controller;


import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.StudentDTO;
import java.util.Map;
import java.util.HashMap;
import com.example.jip.dto.PersonalMaterialDTO;

import com.example.jip.entity.Material;
import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;

import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.PersonalMaterialRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;

import com.example.jip.services.PersonalMaterialServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/studentMaterials")
public class StudentMaterialController {

    @Autowired
    private PersonalMaterialServices personalMaterialServices;  // Service để xử lý logic lưu vào database

    @Autowired
    private TeacherRepository teacherRepository;
    // Endpoint để thêm material
    @Autowired
    private PersonalMaterialRepository personalMaterialRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MaterialRepository materialRepository;

    // Endpoint thêm material và trả về link của material
    @PostMapping("/add")
    public ResponseEntity<String> addMaterial(
            @RequestParam("material_link") String materialLink,
            @RequestParam("student_id") int studentId,
            @RequestParam("material_id") int materialId  // Nhận material_id từ frontend
    ) {
        try {
            // Tìm kiếm Student dựa trên studentId
            Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);

            if (!studentOptional.isPresent()) {
                return ResponseEntity.status(404).body("Student with studentId " + studentId + " not found.");
            }

            Student student = studentOptional.get();

            // Tìm kiếm Material dựa trên materialId
            Optional<Material> materialOptional = materialRepository.findById(materialId);

            if (!materialOptional.isPresent()) {
                return ResponseEntity.status(404).body("Material with materialId " + materialId + " not found.");
            }

            Material material = materialOptional.get();  // Lấy Material từ cơ sở dữ liệu

            // Kiểm tra xem material đã tồn tại hay chưa
            Optional<PersonalMaterial> existingMaterial = personalMaterialRepository
                    .findByStudent_IdAndMaterial_link(student.getId(), materialLink);

            if (existingMaterial.isPresent()) {
                return ResponseEntity.status(409).body("Material đã tồn tại cho học sinh này.");
            }

            // Nếu chưa tồn tại, tạo mới và lưu
            // Khởi tạo PersonalMaterialDTO
            PersonalMaterialDTO personalMaterialDTO = new PersonalMaterialDTO();
            personalMaterialDTO.setMaterial_link(materialLink);

            // Thiết lập StudentDTO vào PersonalMaterialDTO
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(student.getId());
            personalMaterialDTO.setStudent(studentDTO);

            // Tạo MaterialDTO và chỉ gán title từ Material
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setTitle(material.getTitle());  // Lấy title từ Material và gán vào MaterialDTO

            // Gán MaterialDTO vào PersonalMaterialDTO
            personalMaterialDTO.setMaterial(materialDTO);

            // Lưu đối tượng PersonalMaterial vào cơ sở dữ liệu
            PersonalMaterial savedPersonalMaterial = personalMaterialServices.addMaterial(personalMaterialDTO);

            return ResponseEntity.ok(savedPersonalMaterial.getMaterial_link());
        } catch (Exception e) {
            e.printStackTrace();  // Log lỗi chi tiết
            return ResponseEntity.status(500).body("Lỗi khi thêm material: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<PersonalMaterialDTO>> listPersonalMaterials(@RequestParam("student_id") Integer studentId) {
        try {
            // Tìm kiếm Student dựa trên account_id
            Student student = studentRepository.findByAccount_id(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student with studentId " + studentId + " not found."));

            // Tìm danh sách PersonalMaterial dựa trên Student ID
            List<PersonalMaterial> personalMaterials = personalMaterialRepository.findByStudent_Id(student.getId());

            // Chuyển đổi danh sách PersonalMaterial thành PersonalMaterialDTO
            List<PersonalMaterialDTO> materialDTOs = personalMaterials.stream()
                    .map(material -> {
                        PersonalMaterialDTO dto = new PersonalMaterialDTO();
                        dto.setId(material.getId());
                        dto.setMaterial_link(material.getMaterial_link());

                        // Nếu material có đối tượng MaterialDTO, gán title từ MaterialDTO vào
                        if (material.getMaterial() != null) {
                            MaterialDTO materialDTO = new MaterialDTO();
                            materialDTO.setId(material.getMaterial().getId());
                            materialDTO.setTitle(material.getMaterial().getTitle()); // Giả sử getMaterial() trả về đối tượng Material
                            dto.setMaterial(materialDTO); // Gán MaterialDTO vào PersonalMaterialDTO
                        }else {
                            // Gán giá trị mặc định khi material là null
                            MaterialDTO defaultMaterialDTO = new MaterialDTO();
                            defaultMaterialDTO.setTitle("No material available");
                            dto.setMaterial(defaultMaterialDTO);
                        }

                        // Kiểm tra nếu student là null và xử lý
                        if (material.getStudent() != null) {
                            StudentDTO studentDTO = new StudentDTO();
                            studentDTO.setId(material.getStudent().getId());
                            dto.setStudent(studentDTO);  // Gán thông tin student vào DTO nếu có
                        }

                        return dto;
                    }).collect(Collectors.toList());

            // Trả về danh sách DTO
            return ResponseEntity.ok(materialDTOs);
        } catch (IllegalArgumentException e) {
            // Log lỗi và trả về danh sách rỗng
            e.printStackTrace();
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi và trả về danh sách rỗng
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePersonalMaterial(@PathVariable("id") int materialId) {
        try {
            // Tìm PersonalMaterial theo materialId
            Optional<PersonalMaterial> materialOptional = personalMaterialRepository.findById(materialId);

            if (!materialOptional.isPresent()) {
                return ResponseEntity.status(404).body("Material with id " + materialId + " not found.");
            }

            // Xóa tài liệu cá nhân
            personalMaterialRepository.deleteById(materialId);

            return ResponseEntity.ok("Material removed successfully.");
        } catch (Exception e) {
            e.printStackTrace();  // Log lỗi chi tiết
            return ResponseEntity.status(500).body("Error deleting material: " + e.getMessage());
        }
    }


    @GetMapping("/studentMaterials/checkExistence")
    public ResponseEntity<Map<String, Boolean>> checkMaterialExistence(@RequestParam Integer studentId, @RequestParam String materialLink) {
        Optional<PersonalMaterial> existingMaterial = personalMaterialRepository.findByStudent_IdAndMaterial_link(studentId, materialLink);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", existingMaterial.isPresent());
        return ResponseEntity.ok(response);
    }

}