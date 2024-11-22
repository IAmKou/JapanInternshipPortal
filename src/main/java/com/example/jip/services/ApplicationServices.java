package com.example.jip.services;

import com.example.jip.dto.ApplicationDTO;
import com.example.jip.entity.Application;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ApplicationRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ApplicationServices {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

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

    public Application createApplication(ApplicationDTO applicationDTO) {
        Integer teacherId = applicationDTO.getTeacher() != null ? applicationDTO.getTeacher().getId() : null;
        Integer studentId = applicationDTO.getStudent() != null ? applicationDTO.getStudent().getId() : null;

        Application application = new Application();
        application.setName(applicationDTO.getName());
        application.setCategory(applicationDTO.getCategory());
        application.setContent(applicationDTO.getContent());
        application.setImg(applicationDTO.getImg());
        application.setCreated_date(applicationDTO.getCreated_date());
        application.setStatus(Application.Status.Pending);  // Chuyển đổi từ enum của DTO sang Entity
        // Trạng thái mặc định khi tạo mới

        // Nếu có TeacherDTO, tìm Teacher và gán vào Application
        if (teacherId != null) {
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher không tồn tại với ID: " + teacherId));
            application.setTeacher(teacher);
        }

        // Nếu có StudentDTO, tìm Student và gán vào Application
        if (studentId != null) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student không tồn tại với ID: " + studentId));
            application.setStudent(student);
        }

        return applicationRepository.save(application);
    }

}
