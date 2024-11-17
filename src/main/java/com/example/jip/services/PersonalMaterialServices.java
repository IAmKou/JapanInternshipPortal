package com.example.jip.services;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.dto.response.PersonalMaterialDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.PersonalMaterial;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.PersonalMaterialRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PersonalMaterialServices {

    @Autowired
    private PersonalMaterialRepository personalMaterialRepository;

    @Autowired
    private StudentRepository studentRepository;

    public PersonalMaterial addMaterial(PersonalMaterialDTO personalMaterialDTO) {
        Integer studentId = personalMaterialDTO.getStudent().getId();
        Student student = studentRepository.findById(personalMaterialDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student không tồn tại với ID: " + personalMaterialDTO.getStudent().getId()));

        PersonalMaterial personalMaterial = new PersonalMaterial();
        personalMaterial.setMaterial_link(personalMaterialDTO.getMaterial_link());
        personalMaterial.setStudent(student);

        return personalMaterialRepository.save(personalMaterial); // Sửa lỗi ở đây
    }

}
