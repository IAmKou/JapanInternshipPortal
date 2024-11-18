package com.example.jip.controller;


import com.example.jip.dto.StudentDTO;
import java.util.Map;
import java.util.HashMap;
import com.example.jip.dto.PersonalMaterialDTO;

import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;

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

    // Endpoint thêm material và trả về link của material
    @PostMapping("/add")
    public ResponseEntity<String> addMaterial(
            @RequestParam("material_link") String materialLink,
            @RequestParam("student_id") int studentId
    ) {
        try {
            // Tìm kiếm Student dựa trên account_id
            Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);

            if (!studentOptional.isPresent()) {
                return ResponseEntity.status(404).body("Student with studentId " + studentId + " not found.");
            }

            Student student = studentOptional.get();

            // Kiểm tra xem material đã tồn tại hay chưa
            Optional<PersonalMaterial> existingMaterial = personalMaterialRepository
                    .findByStudent_IdAndMaterial_link(student.getId(), materialLink);

            if (existingMaterial.isPresent()) {
                // Trả về thông báo rõ ràng nếu material đã tồn tại
                return ResponseEntity.status(409).body("Material đã tồn tại cho học sinh này.");
            }

            // Nếu chưa tồn tại, tạo mới và lưu
            PersonalMaterialDTO personalMaterialDTO = new PersonalMaterialDTO();
            personalMaterialDTO.setMaterial_link(materialLink);

            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(student.getId());
            personalMaterialDTO.setStudent(studentDTO);

            // Lưu đối tượng PersonalMaterial vào cơ sở dữ liệu
            PersonalMaterial savedPersonalMaterial = personalMaterialServices.addMaterial(personalMaterialDTO);

            return ResponseEntity.ok(savedPersonalMaterial.getMaterial_link());
        } catch (Exception e) {
            e.printStackTrace();  // Log lỗi chi tiết
            return ResponseEntity.status(500).body("Material đã tồn tại . Lỗi: " + e.getMessage());
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
        Optional<PersonalMaterial> personalMaterialOptional = personalMaterialRepository.findById(materialId);

        if (!personalMaterialOptional.isPresent()) {
            return ResponseEntity.status(404).body("PersonalMaterial with id " + materialId + " not found.");
        }

        personalMaterialRepository.deleteById(materialId);
        return ResponseEntity.ok("Xóa tài liệu thành công!");  // Trả về phản hồi thành công
    }

    @GetMapping("/studentMaterials/checkExistence")
    public ResponseEntity<Map<String, Boolean>> checkMaterialExistence(@RequestParam Integer studentId, @RequestParam String materialLink) {
        Optional<PersonalMaterial> existingMaterial = personalMaterialRepository.findByStudent_IdAndMaterial_link(studentId, materialLink);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", existingMaterial.isPresent());
        return ResponseEntity.ok(response);
    }

}