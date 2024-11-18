package com.example.jip.services;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.PersonalMaterialDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.PersonalMaterialRepository;
import com.example.jip.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonalMaterialServices {

    @Autowired
    private PersonalMaterialRepository personalMaterialRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public PersonalMaterial addMaterial(PersonalMaterialDTO personalMaterialDTO) {
        Integer studentId = personalMaterialDTO.getStudent().getId();
        Student student = studentRepository.findById(personalMaterialDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student không tồn tại với ID: " + personalMaterialDTO.getStudent().getId()));

        PersonalMaterial personalMaterial = new PersonalMaterial();
        personalMaterial.setMaterial_link(personalMaterialDTO.getMaterial_link());
        personalMaterial.setStudent(student);

        MaterialDTO materialDTO = personalMaterialDTO.getMaterial();
        Optional<Material> materialOptional = materialRepository.findByTitle(materialDTO.getTitle());

        if (!materialOptional.isPresent()) {
            // Nếu không có, tạo mới material và lưu vào cơ sở dữ liệu
            Material material = new Material();
            material.setTitle(materialDTO.getTitle());

            // Lưu Material vào cơ sở dữ liệu
            material = materialRepository.save(material);
            personalMaterial.setMaterial(material);  // Thiết lập đối tượng Material cho PersonalMaterial
        } else {
            // Nếu có sẵn material, lấy thông tin từ cơ sở dữ liệu
            personalMaterial.setMaterial(materialOptional.get());
        }

        return personalMaterialRepository.save(personalMaterial); // Sửa lỗi ở đây
    }


}
