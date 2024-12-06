package com.example.jip.services;

import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentGradeRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
import com.example.jip.entity.*;

import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import com.example.jip.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices extends AssignmentCreationRequest {

    AssignmentRepository assignmentRepository;

    TeacherRepository teacherRepository;

    CloudinaryService cloudinaryService;

    ClassRepository classRepository;

    AssignmentClassRepository assignmentClassRepository;

    AssignmentStudentRepository assignmentStudentRepository;

    StudentAssignmentRepository studentAssignmentRepository;


    @PreAuthorize("hasAuthority('TEACHER')")
    public List<AssignmentResponse> getAllAssignmentByTeacherId(int teacherId) {
        return assignmentRepository.findAssignmentsByTeacherId(teacherId).stream()
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
                    response.setDescription(assignment.getDescription());
                    response.setContent(assignment.getContent());
                    response.setFolder(assignment.getImgUrl());
                    response.setClasses(
                            assignment.getClasses().stream()
                                    .map(Class::getName)
                                    .collect(Collectors.toList())
                    );
                    return response;
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public Assignment createAssignment(AssignmentCreationRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher ID not found: " + request.getTeacher().getId()));

        // Create Assignment entity
        Assignment assignment = new Assignment();
        assignment.setCreated_date(request.getCreated_date());
        assignment.setEnd_date(request.getEnd_date());
        assignment.setDescription(request.getDescription());
        assignment.setContent(request.getContent());


        // Handle file uploads
        MultipartFile[] imgFiles = request.getImgFile();
        for (int i = 0; i < request.getImgFile().length; i++) {
            log.info("Uploading file: " + request.getImgFile()[i].getOriginalFilename());
        }
        if (imgFiles.length > 1) {
            // Sanitize and create folder name
            String folderName = sanitizeFolderName("assignments/" + request.getDescription());
            assignment.setImgUrl(folderName); // Set folder URL
            uploadFilesToFolder(imgFiles, folderName);
        } else {
            assignment.setImgUrl(null); // Ensure `imgUrl` is null if no files are provided
        }
        // Save assignment
        Assignment savedAssignment = assignmentRepository.save(assignment);

        // Assign the assignment to classes and students
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            List<Class> classes = classRepository.findByTeacher_Id(request.getTeacher().getId());
            for (Class clas : classes) {
                // Link assignment to class
                assignmentClassRepository.save(new AssignmentClass(savedAssignment, clas));

                // Link assignment to all students in the class
                for (Listt listEntry : clas.getClassLists()) {
                    Student student = listEntry.getStudent();
                    assignmentStudentRepository.save(new AssignmentStudent(savedAssignment, student));
                }
            }
        }

        assignment.setTeacher(teacher);

        // Save the assignment
        return assignmentRepository.save(assignment);
    }




    public AssignmentResponse getAssignmentById(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + assignmentId));

        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setDescription(assignment.getDescription());
        response.setContent(assignment.getContent());
        response.setCreated_date(assignment.getCreated_date());
        response.setEnd_date(assignment.getEnd_date());
        response.setClasses(assignment.getClasses().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));

        // Retrieve file URLs from Cloudinary
        String folderName = assignment.getImgUrl();
        try {
            List<Map<String, Object>> resources = cloudinaryService.getFilesFromFolder(folderName);
            List<String> fileUrls = resources.stream()
                    .map(resource -> (String) resource.get("url"))
                    .collect(Collectors.toList());

            if (fileUrls.isEmpty()) {
                log.warn("No files found for assignment with ID: {}", assignmentId);
            }
            response.setFiles(fileUrls);

        } catch (Exception e) {
            log.error("Error retrieving files for assignment with ID: {}", assignmentId, e);
            response.setFiles(Collections.emptyList());
        }

        return response;
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public void deleteAssignmentById(int assignmentId) {
        // Find assignment or throw exception if not found
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + assignmentId));

        // Delete related rows in dependent tables
        assignmentStudentRepository.deleteByAssignmentId(assignmentId);
        assignmentClassRepository.deleteByAssignmentId(assignmentId);
        studentAssignmentRepository.deleteByAssignmentId(assignmentId);

        // Delete associated cloud resources if applicable

        if(assignment.getImgUrl() != null) {
            String folderPath = sanitizeFolderName(assignment.getImgUrl());
            cloudinaryService.deleteFolder(folderPath);
        }

        // Finally, delete the assignment
        assignmentRepository.delete(assignment);
    }

    private void uploadFilesToFolder(MultipartFile[] files, String folderName) {
        Set<String> uploadedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty() && uploadedFiles.add(file.getOriginalFilename())) {
                try {
                    FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                    cloudinaryService.uploadFileToFolder(file, folderName);
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
    @Transactional
    public Assignment updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));

        MultipartFile[] newFiles = request.getImgFile();
        String folderName = sanitizeFolderName("assignments/" + assignment.getDescription()); //Folder name = assignment's description

        if (folderName.isEmpty()) {
            throw new RuntimeException("Folder name is not set for assignment ID: " + assignmentId);
        }

        // Append new files if provided
        if (newFiles != null) {
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                    cloudinaryService.uploadFileToFolder(file, folderName);
                }
            }
        }

        // **Clear existing class associations**
        assignmentClassRepository.deleteByAssignmentId(assignment.getId());

        // Add new class associations
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                // Find the class
                Class clas = classRepository.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found: " + classId));

                // Link assignment to class
                assignmentClassRepository.save(new AssignmentClass(assignment, clas));

                // Link assignment to all students in the class
                for (Listt listEntry : clas.getClassLists()) {
                    Student student = listEntry.getStudent();
                    assignmentStudentRepository.save(new AssignmentStudent(assignment, student));
                }
            }
        }
        // Update other fields
        if (request.getEnd_date() != null) {
            assignment.setEnd_date(request.getEnd_date());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            assignment.setContent(request.getContent());
        }
        return assignmentRepository.save(assignment);
    }




    public void deleteFile(FileDeleteRequest request){
    // Sanitize folder name and delete the file
        String folderName = sanitizeFolderName("assignments/" + assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new NoSuchElementException("Assignment not found!"))
                .getDescription());

        cloudinaryService.deleteFile(request.getFileUrl(), folderName);
    }


}

