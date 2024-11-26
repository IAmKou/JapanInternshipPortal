package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Class;

import com.example.jip.entity.Teacher;
import com.example.jip.exception.CloudinaryFolderAccessException;
import com.example.jip.exception.FuncErrorException;
import com.example.jip.exception.InvalidImageUrlException;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices {

    AssignmentRepository assignmentRepository;

    TeacherRepository teacherRepository;

    CloudinaryService cloudinaryService;

    ClassRepository classRepository;


    @PreAuthorize("hasAuthority('TEACHER')")
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
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

        // Sanitize and create folder name
        String folderName = sanitizeFolderName("assignments/" + request.getDescription());
        assignment.setImgUrl(folderName); // Set folder URL

        // Handle file uploads
        MultipartFile[] imgFiles = request.getImgFile();
        if (imgFiles != null && imgFiles.length > 0) {
            uploadFilesToFolder(imgFiles, folderName);
        }

        // Handle class associations
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            Set<Class> classes = request.getClassIds().stream()
                    .map(classId -> classRepository.findById(classId)
                            .orElseThrow(() -> new RuntimeException("Class not found: " + classId)))
                    .collect(Collectors.toSet());
            assignment.setClasses(classes);
        }

        assignment.setTeacher(teacher);

        // Save the assignment
        return assignmentRepository.save(assignment);
    }




    @PreAuthorize("hasAuthority('TEACHER')")
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
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + assignmentId));

        String folderPath = sanitizeFolderName(assignment.getImgUrl());
        cloudinaryService.deleteFolder(folderPath);

        assignmentRepository.deleteById(assignmentId);
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

        if (folderName == null || folderName.isEmpty()) {
            throw new RuntimeException("Folder name is not set for assignment ID: " + assignmentId);
        }


        // Append new files if provided
        if (newFiles != null && newFiles.length > 0) {
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
                    cloudinaryService.uploadFileToFolder(file, folderName);
                }
            }
        }

        // Update classes if provided
        if (request.getClassIds() != null) {
            Set<Class> classes = new HashSet<>();
            for (Integer classId : request.getClassIds()) {
                Class classEntity = classRepository.findById(classId)
                        .orElseThrow(() -> new NoSuchElementException("Class not found: " + classId));
                classes.add(classEntity);
            }
            assignment.setClasses(classes); // Assuming Assignment entity has `classes` field
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

    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteFile(FileDeleteRequest request){
    // Sanitize folder name and delete the file
        String folderName = sanitizeFolderName("assignments/" + assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new NoSuchElementException("Assignment not found!"))
                .getDescription());

        cloudinaryService.deleteFile(request.getFileUrl(), folderName);
    }

}

