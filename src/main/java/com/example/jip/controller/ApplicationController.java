package com.example.jip.controller;

import com.example.jip.dto.ApplicationDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.entity.Application;
import com.example.jip.entity.Attendant;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ApplicationRepository;
import com.example.jip.repository.AttendantRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.ApplicationServices;
import com.example.jip.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationServices applicationServices;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AttendantRepository attendantRepository;

    @PostMapping("/create")
    public RedirectView createApplication(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("content") String content,
            @RequestParam(value = "imgFile", required = false) MultipartFile imgFile,
            @RequestParam(value = "teacher_id", required = false) Integer teacherId,
            @RequestParam(value = "student_id", required = false) Integer studentId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setCreated_date(new Date());
            applicationDTO.setName(name);
            applicationDTO.setCategory(category);
            applicationDTO.setContent(content);



            // Kiểm tra và lấy teacher_id nếu có, nếu không thì lấy student_id
            if (teacherId != null) {
                Optional<Teacher> teacherOptional = teacherRepository.findByAccount_id(teacherId);
                if (teacherOptional.isPresent()) {
                    Teacher teacher = teacherOptional.get();
                    TeacherDTO teacherDTO = new TeacherDTO();
                    teacherDTO.setId(teacher.getId());
                    applicationDTO.setTeacher(teacherDTO);
                    // Upload image and set it to applicationDTO
                    if (imgFile != null) {
                        String fileName = imgFile.getOriginalFilename(); // Lấy tên file gốc
                        String folderName = "Applications/" + applicationDTO.getName(); // Đường dẫn thư mục tổng và con

                        // Tải file lên S3
                        String img = s3Service.uploadFile(imgFile, folderName, fileName);

                        // Cập nhật URL ảnh vào materialDTO
                        applicationDTO.setImg(img);
                    }
                } else {
                    redirectAttributes.addAttribute("error", "Teacher with ID " + teacherId + " not found.");
                    return new RedirectView("/Send-application.html");  // Chuyển hướng lại nếu lỗi
                }
            } else if (studentId != null) {
                Optional<Student> studentOptional = studentRepository.findByAccount_id(studentId);
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setId(student.getId());
                    applicationDTO.setStudent(studentDTO);
                    // Upload image and set it to applicationDTO
                    if (imgFile != null) {
                        String fileName = imgFile.getOriginalFilename(); // Lấy tên file gốc
                        String folderName = "Applications/" + applicationDTO.getName(); // Đường dẫn thư mục tổng và con

                        // Tải file lên S3
                        String img = s3Service.uploadFile(imgFile, folderName, fileName);

                        // Cập nhật URL ảnh vào materialDTO
                        applicationDTO.setImg(img);
                    }
                } else {
                    redirectAttributes.addAttribute("error", "Student with ID " + studentId + " not found.");
                    return new RedirectView("/Send-application.html");  // Chuyển hướng lại nếu lỗi
                }
            }

            if (teacherId == null && studentId == null) {
                redirectAttributes.addAttribute("error", "Both Teacher ID and Student ID must be provided.");
                return new RedirectView("/Send-application.html");  // Chuyển hướng lại nếu thiếu ID
            }

            applicationDTO.setStatus(ApplicationDTO.Status.Pending); // Sử dụng setter
            applicationDTO.setReply("");
            applicationDTO.setReplied_date(null);

            // Lưu Application
            Application savedApplication = applicationServices.createApplication(applicationDTO);

            // Trả về redirect với thông báo thành công
            redirectAttributes.addAttribute("message", "Application '" + applicationDTO.getCategory() + "' created successfully with ID: " + savedApplication.getId());
            return new RedirectView("/View-my-application.html");  // Chuyển hướng thành công

        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", "Failed to create application: " + e.getMessage());
            return new RedirectView("/Send-application.html");  // Chuyển hướng nếu có lỗi
        }
    }


    @GetMapping("/list")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications(
            @RequestParam(value = "teacher_id", required = false) Integer teacherId,
            @RequestParam(value = "student_id", required = false) Integer studentId) {

        List<ApplicationDTO> applicationDTOs = new ArrayList<>();
        try {
            // Kiểm tra nếu teacherId được truyền vào
            if (teacherId != null) {
                Teacher teacher = teacherRepository.findByAccount_id(teacherId)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher with ID " + teacherId + " not found."));

                // Lọc các yêu cầu của giáo viên
                List<Application> applications = applicationRepository.findByTeacher_Id(teacher.getId());
                applicationDTOs = applications.stream()
                        .map(application -> {
                            ApplicationDTO dto = new ApplicationDTO();
                            dto.setId(application.getId());
                            dto.setName(application.getName());
                            dto.setCategory(application.getCategory());
                            dto.setCreated_date(application.getCreated_date());
                            dto.setReply(application.getReply());
                            dto.setContent(application.getContent());
                            dto.setStatus(ApplicationDTO.toDTOStatus(application.getStatus()));
                            dto.setReplied_date(application.getReplied_date());
                            String folderName = application.getImg(); // Lấy tên thư mục từ database (imgUrl)
                            try {
                                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                                if (fileUrls.isEmpty()) {
                                    System.out.println("No files found for application with ID: " + application.getId());
                                }
                                dto.setFiles(fileUrls);  // Gọi phương thức để chuyển đổi List<String> thành String

                            } catch (Exception e) {
                                System.err.println("Error retrieving files for application with ID: " + application.getId());
                                e.printStackTrace();
                                dto.setImgFromList(Collections.emptyList()); // Trả về danh sách rỗng nếu có lỗi
                            }
                            return dto;
                        }).collect(Collectors.toList());
            }
            // Kiểm tra nếu studentId được truyền vào
            else if (studentId != null) {
                Student student = studentRepository.findByAccount_id(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found."));

                // Lọc các yêu cầu của học sinh
                List<Application> applications = applicationRepository.findByStudent_Id(student.getId());
                applicationDTOs = applications.stream()
                        .map(application -> {
                                    ApplicationDTO dto = new ApplicationDTO();
                                    dto.setId(application.getId());
                                    dto.setName(application.getName());
                                    dto.setCategory(application.getCategory());
                                    dto.setCreated_date(application.getCreated_date());
                                    dto.setReply(application.getReply());
                                    dto.setContent(application.getContent());
                                    dto.setStatus(ApplicationDTO.toDTOStatus(application.getStatus()));
                                    dto.setReplied_date(application.getReplied_date());
                            String folderName = application.getImg(); // Lấy tên thư mục từ database (imgUrl)
                            try {
                                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                                if (fileUrls.isEmpty()) {
                                    System.out.println("No files found for application with ID: " + application.getId());
                                }
                                dto.setFiles(fileUrls);  // Gọi phương thức để chuyển đổi List<String> thành String

                            } catch (Exception e) {
                                System.err.println("Error retrieving files for application with ID: " + application.getId());
                                e.printStackTrace();
                                dto.setImgFromList(Collections.emptyList()); // Trả về danh sách rỗng nếu có lỗi
                            }
                                    return dto;
                                }).collect(Collectors.toList());
            }
            // Nếu không có teacherId hoặc studentId, lấy tất cả các yêu cầu
            else {
                List<Application> applications = applicationRepository.findAll();
                applicationDTOs = applications.stream()
                        .map(application -> {
                            ApplicationDTO dto = new ApplicationDTO();
                            dto.setId(application.getId());
                            dto.setName(application.getName());
                            dto.setCategory(application.getCategory());
                            dto.setCreated_date(application.getCreated_date());
                            dto.setReply(application.getReply());
                            dto.setContent(application.getContent());
                            dto.setStatus(ApplicationDTO.toDTOStatus(application.getStatus()));
                            dto.setReplied_date(application.getReplied_date());

                            String folderName = application.getImg(); // Lấy tên thư mục từ database (imgUrl)
                            try {
                                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                                if (fileUrls.isEmpty()) {
                                    System.out.println("No files found for application with ID: " + application.getId());
                                }
                                dto.setFiles(fileUrls);  // Gọi phương thức để chuyển đổi List<String> thành String

                            } catch (Exception e) {
                                System.err.println("Error retrieving files for application with ID: " + application.getId());
                                e.printStackTrace();
                                dto.setImgFromList(Collections.emptyList()); // Trả về danh sách rỗng nếu có lỗi
                            }

                            return dto;
                        }).collect(Collectors.toList());
            }

            return ResponseEntity.ok(applicationDTOs);
        } catch (IllegalArgumentException e) {
            // Log lỗi và trả về thông báo lỗi cho người dùng
            e.printStackTrace();
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationDetails(@PathVariable("id") Integer applicationId) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setId(application.getId());
            applicationDTO.setName(application.getName());
            applicationDTO.setCategory(application.getCategory());
            applicationDTO.setCreated_date(application.getCreated_date());
            applicationDTO.setReply(application.getReply());
            applicationDTO.setContent(application.getContent());
            applicationDTO.setStatus(ApplicationDTO.toDTOStatus(application.getStatus()));
            applicationDTO.setReplied_date(application.getReplied_date());
            applicationDTO.setImg(application.getImg());
            return ResponseEntity.ok(applicationDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateApplication(
            @PathVariable("id") Integer applicationId,
            @RequestBody Map<String, Object> payload) {

        if (applicationId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "ID ứng dụng không hợp lệ!"));
        }

        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if (!applicationOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Ứng dụng không tồn tại!"));
        }

        Application application = applicationOptional.get();


        // Lấy giá trị từ payload
        if (payload.containsKey("status")) {
            String status = (String) payload.get("status");
            try {
                application.setStatus(Application.Status.valueOf(status));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid status value"));
            }
        }

        if (payload.containsKey("reply")) {
            application.setReply((String) payload.get("reply"));
        }


        Student student = studentRepository.findById(application.getStudent().getId()).orElse(null);
        student.setMark(true);

        application.setReplied_date(new Date());
        studentRepository.save(student);
        applicationRepository.save(application);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Application reply success",
                "redirect", "/View-list-application.html"
        ));

    }
    private void uploadFilesToFolder(MultipartFile[] files, String folderName) {
        Set<String> uploadedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty() && uploadedFiles.add(file.getOriginalFilename())) {
                try {
                    String sanitizedFolderName = sanitizeFolderName(folderName);
                    s3Service.uploadFile(file, sanitizedFolderName, file.getOriginalFilename());
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
