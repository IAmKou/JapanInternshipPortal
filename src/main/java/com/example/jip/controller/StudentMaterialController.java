package com.example.jip.controller;


import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.StudentDTO;

import com.example.jip.dto.response.PersonalMaterialDTO;

import com.example.jip.entity.Material;
import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;

import com.example.jip.repository.PersonalMaterialRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;

import com.example.jip.services.PersonalMaterialServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            @RequestParam("material_link") String materialLink, // Tham số material_link từ request
            @RequestParam("student_id") int studentId
    ) {
        try {
            // Tìm kiếm Student dựa trên account_id
            Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);

            // Kiểm tra nếu Student không tồn tại
            if (!studentOptional.isPresent()) {
                return ResponseEntity.status(404).body("Student with studentId " + studentId + " not found.");
            }

            // Lấy đối tượng Student từ Optional
            Student student = studentOptional.get();

            // Tạo đối tượng PersonalMaterial và gán giá trị từ request
            PersonalMaterialDTO personalMaterialDTO = new PersonalMaterialDTO();
            personalMaterialDTO.setMaterial_link(materialLink);


            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(student.getId());
            personalMaterialDTO.setStudent(studentDTO);

            // Lưu đối tượng PersonalMaterial vào cơ sở dữ liệu
            PersonalMaterial savedPersonalMaterial = personalMaterialServices.addMaterial(personalMaterialDTO);

            // Trả về link của tài liệu sau khi lưu
            return ResponseEntity.ok(savedPersonalMaterial.getMaterial_link());
        } catch (Exception e) {
            e.printStackTrace();  // Log lỗi chi tiết
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi thêm material. Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<PersonalMaterialDTO>> listPersonalMaterials(@RequestParam("student_id") Integer studentId) {
        try {
            // Lấy tất cả các PersonalMaterial có userID bằng với studentId
            List<PersonalMaterial> personalMaterials = personalMaterialRepository.findByStudent_Id(studentId);

            // Chuyển đổi danh sách PersonalMaterial sang DTO
            List<PersonalMaterialDTO> materialDTOs = personalMaterials.stream()
                    .map(material -> {
                        PersonalMaterialDTO dto = new PersonalMaterialDTO();
                        dto.setId(material.getId());
                        dto.setMaterial_link(material.getMaterial_link());
                        return dto;
                    }).collect(Collectors.toList());

            // Trả về danh sách materials dưới dạng ResponseEntity
            return ResponseEntity.ok(materialDTOs);
        } catch (Exception e) {
            // Xử lý các lỗi khác
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Trả về null nếu có lỗi
        }
    }
}