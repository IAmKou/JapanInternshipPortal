package com.example.jip.services;

import com.example.jip.dto.MaterialDTO;
import com.example.jip.entity.Material;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.MaterialRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@Service
public class MaterialServices {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // Phương thức để lưu ảnh và trả về tên ảnh
    public String saveImage(MultipartFile img) throws IOException {
        if (img.isEmpty()) {
            throw new IOException("File is empty");
        }

        // Đặt tên cho file (có thể tạo tên độc nhất bằng cách sử dụng thời gian hiện tại)
        String imgFileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();

        // Xác định đường dẫn để lưu ảnh (có thể thay đổi theo thư mục bạn muốn lưu ảnh)
        Path path = Paths.get("uploads/" + imgFileName);

        // Lưu ảnh vào thư mục
        Files.copy(img.getInputStream(), path);

        return imgFileName;  // Trả về tên file đã lưu
    }

    public Material createMaterial(MaterialDTO materialDTO) {
        Integer teacherId = materialDTO.getTeacher().getId();  // TeacherDTO có trường teacherId
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