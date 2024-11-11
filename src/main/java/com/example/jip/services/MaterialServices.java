package com.example.jip.services;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class MaterialServices {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TeacherRepository teacherRepository;



    public Material createMaterial(MaterialDTO materialDTO) {
        Teacher teacher = teacherRepository.findById(materialDTO.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher không tồn tại với ID: " + materialDTO.getTeacher().getId()));

        Material material = new Material();
        material.setTitle(materialDTO.getTitle());
        material.setContent(materialDTO.getContent());
        material.setImg(materialDTO.getImg()); // Lưu img dưới dạng String
        material.setTeacher(teacher);
        material.setCreated_date(new Date());

        return materialRepository.save(material);
    }

}