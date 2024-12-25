package com.example.jip.services;



import com.example.jip.dto.request.assignment.FileDeleteRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentFileDeleteRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentGradeRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentSubmitRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentUpdateRequest;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Student;
import com.example.jip.entity.StudentAssignment;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.StudentAssignmentRepository;
import com.example.jip.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentAssignmentServices {


    StudentAssignmentRepository studentAssignmentRepository;

    AssignmentRepository assignmentRepository;

    StudentRepository studentRepository;

    S3Service s3Service;

    NotificationServices notificationServices;




    @PreAuthorize("hasAuthority('STUDENT')")
    public StudentAssignment submitAssignment(StudentAssignmentSubmitRequest request){
        Student student = studentRepository.findById(request.getStudent().getId())
               .orElseThrow(() -> new RuntimeException("Student ID not found: " + request.getStudent().getId()));

        Assignment assignment = assignmentRepository.findById(request.getAssignment().getId())
               .orElseThrow(() -> new RuntimeException("Assignment ID not found: " + request.getAssignment().getId()));

        Date today = new Date();
        StudentAssignment studentAssignment = new StudentAssignment();
        studentAssignment.setDate(today);
        studentAssignment.setDescription(request.getDescription());
        studentAssignment.setContent(request.getContent());
        studentAssignment.setStudent(student);
        studentAssignment.setStatus(StudentAssignment.Status.SUBMITTED);
        studentAssignment.setAssignment(assignment);

        // Sanitize and create folder name
        String folderName = sanitizeFolderName("submissions/" + request.getDescription() + "_" + student.getFullname());
        studentAssignment.setFile_url(folderName); // Set folder URL
        if (request.getImgFile() != null) {
            MultipartFile[] imgFiles = request.getImgFile();
            for (int i = 0; i < request.getImgFile().length; i++) {
                log.info("Uploading file: " + request.getImgFile()[i].getOriginalFilename());
            }
            uploadFilesToFolder(imgFiles, folderName);

        } else {
            log.warn("No files provided in the request.");
        }

        notificationServices.createAutoNotificationForAssignment(student.getFullname() + "has submitted an assignment",student.getId(),assignment.getTeacher().getId());

        return studentAssignmentRepository.save(studentAssignment);
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

    @PreAuthorize("hasAuthority('TEACHER')")
    public StudentAssignment gradeSubmittedAssignment(int studentAssignmentId, StudentAssignmentGradeRequest request) {
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(studentAssignmentId)
                .orElseThrow(() -> new NoSuchElementException("studentAssignmentId not found!"));

        if(request.getMark() != null){
            studentAssignment.setMark(request.getMark());
        }

        if (request.getStatus() != null) {
            studentAssignment.setStatus(request.getStatus());
        }
        log.info("Grading assignment with Mark: " + request.getMark() + ", Status: " + request.getStatus());
        return studentAssignmentRepository.save(studentAssignment);
    }

    public List<StudentAssignmentResponse> getSubmittedAssignmentsByStudentId(int studentId) {
        List<StudentAssignment> studentAssignments = studentAssignmentRepository.findAllByStudentId(studentId);
        if(studentAssignments == null){
             throw new NoSuchElementException("Submitted assignment not found");
        } else {
            // Map to DTOs
            List<StudentAssignmentResponse> responses = studentAssignments.stream()
                    .map(sa -> {
                        StudentAssignmentResponse response = new StudentAssignmentResponse();
                        response.setId(sa.getId());
                        response.setMark(sa.getMark());
                        response.setDescription(sa.getDescription());
                        response.setContent(sa.getContent());
                        response.setDate(sa.getDate());
                        response.setStatus(sa.getStatus().toString());
                        response.setAssignmentId(sa.getAssignment().getId());
                        response.setStudentId(sa.getStudent().getId());
                        response.setAssignmentName(sa.getAssignment().getDescription());

                        return response;
                    })
                    .collect(Collectors.toList());
            return responses;
        }
    }

    public StudentAssignmentResponse getStudentAssignmentDetail(int studentAssignmentId) {
        // Fetch the StudentAssignment entity from the repository
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(studentAssignmentId)
                .orElseThrow(() -> new NoSuchElementException("StudentAssignment not found"));

        // Map the entity to the response DTO
        StudentAssignmentResponse response = new StudentAssignmentResponse();
        response.setId(studentAssignment.getId());
        response.setDescription(studentAssignment.getDescription());
        response.setContent(studentAssignment.getContent());
        response.setDate(studentAssignment.getDate());
        response.setMark(studentAssignment.getMark());
        response.setStatus(studentAssignment.getStatus().toString());
        response.setAssignmentId(studentAssignment.getAssignment().getId()); // Includes assignment details
        response.setStudentId(studentAssignment.getStudent().getId()); // Includes student details
        response.setStudentName(studentAssignment.getStudent().getFullname()); // Includes student
        String folderName = studentAssignment.getFile_url();
        try {
            if(!folderName.isEmpty()){
                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                if (fileUrls.isEmpty()) {
                    log.warn("No files found for assignment with ID: {}", response.getId());
                }
                response.setFiles(fileUrls);
            }

        } catch (NullPointerException ne){
            log.error("No file found");
        }
        catch (Exception e) {
            log.error("Error retrieving files for assignment with ID: {}", response.getId(), e);
            response.setFiles(Collections.emptyList());
        }
        return response;
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public List<StudentAssignmentResponse> getSubmittedAssignmentsByAssignmentId(int assignmentId) {
        List<StudentAssignment> studentAssignments = studentAssignmentRepository.findByAssignmentId(assignmentId);
        List<StudentAssignmentResponse> responses = studentAssignments.stream()
                // Map to DTOs
                .map(sa -> {
                    StudentAssignmentResponse response = new StudentAssignmentResponse();
                    response.setId(sa.getId());
                    response.setMark(sa.getMark());
                    response.setDescription(sa.getDescription());
                    response.setContent(sa.getContent());
                    response.setDate(sa.getDate());
                    response.setAssignmentId(sa.getAssignment().getId());
                    response.setStudentId(sa.getStudent().getId());
                    response.setStudentName(sa.getStudent().getFullname()); // Thêm tên học sinh vào response
                    return response;
                })
                .collect(Collectors.toList());
        return responses;
    }


    @PreAuthorize("hasAuthority('STUDENT')")
    public StudentAssignment updateStudentAssignment(int submittedAssignmentId, StudentAssignmentUpdateRequest request) {
        StudentAssignment existingAssignment = studentAssignmentRepository.findById(submittedAssignmentId)
                .orElseThrow(() -> new RuntimeException("StudentAssignment not found"));

        if (existingAssignment.getStatus() == StudentAssignment.Status.MARKED) {
            throw new RuntimeException("Cannot edit a marked assignment.");
        }

        existingAssignment.setDescription(request.getDescription());
        existingAssignment.setContent(request.getContent());

        Student student = studentRepository.findById(existingAssignment.getStudent().getId())
                .orElseThrow(() -> new NoSuchElementException("Student not found"));
        if (request.getImgFile() != null) {
            MultipartFile[] newFiles = request.getImgFile();
            for (int i = 0; i < request.getImgFile().length; i++) {
                log.info("Uploading file: " + request.getImgFile()[i].getOriginalFilename());
            }
            if (newFiles.length > 0) {
                String folderName = sanitizeFolderName("submissions/" + request.getDescription() + "_" + student.getFullname());
                if (folderName.isEmpty()) {
                    throw new RuntimeException("Folder name is not set for student_assignment ID: " + existingAssignment.getId());
                }
                uploadFilesToFolder(newFiles, folderName);
                existingAssignment.setFile_url(folderName); // Set folder URL
            }
        } else {
            log.warn("No files provided in the request.");
        }

        return studentAssignmentRepository.save(existingAssignment);
    }

    public void deleteFile(StudentAssignmentFileDeleteRequest request){
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(request.getStudentAssignmentId())
                .orElseThrow(() -> new RuntimeException("StudentAssignment not found"));
        // Sanitize folder name and delete the file
        String folderName = sanitizeFolderName("submissions/" + studentAssignmentRepository.findById(request.getStudentAssignmentId())
                .orElseThrow(() -> new NoSuchElementException("Student Assignment not found!"))
                .getDescription() + "_" + studentAssignment.getStudent().getFullname());

        s3Service.deleteFile(request.getFileUrl(), folderName);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public void deleteStudentAssignment(int submittedAssignmentId) {
        StudentAssignment existingAssignment = studentAssignmentRepository.findById(submittedAssignmentId)
                .orElseThrow(() -> new RuntimeException("StudentAssignment not found"));

        if (existingAssignment.getStatus() == StudentAssignment.Status.MARKED) {
            throw new RuntimeException("Cannot delete a marked assignment.");
        }

        // Delete associated cloud resources if applicable
        if(existingAssignment.getFile_url() != null) {
            String folderPath = sanitizeFolderName(existingAssignment.getFile_url());
            s3Service.deleteFolder(folderPath);
        }

        studentAssignmentRepository.delete(existingAssignment);
    }

}
