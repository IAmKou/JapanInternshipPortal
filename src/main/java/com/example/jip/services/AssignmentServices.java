package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Class;

import com.example.jip.entity.Teacher;
import com.example.jip.exception.CloudinaryFolderAccessException;
import com.example.jip.exception.InvalidImageUrlException;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
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
                .orElseThrow(() -> new RuntimeException("Teacher ID not found : " + request.getTeacher().getId()));
//            FileUploadUtil.assertAllowed(request.getImgFile(), FileUploadUtil.IMAGE_PATTERN);

        // Convert the request to an Assignment entity
        Assignment assignment = new Assignment();
        assignment.setCreated_date(request.getCreated_date());
        assignment.setEnd_date(request.getEnd_date());
        assignment.setDescription(request.getDescription());
        assignment.setContent(request.getContent());


        String folderName = assignment.getDescription(); // Create a folder path

        MultipartFile[] imgFiles = request.getImgFile();
        if (imgFiles != null && imgFiles.length > 0) {
            Set<String> uploadedFiles = new HashSet<>();
            for (MultipartFile imgFile : imgFiles) {
                if (!imgFile.isEmpty() && uploadedFiles.add(imgFile.getOriginalFilename())) {
                    cloudinaryService.uploadFileToFolder(imgFile, folderName);
                } else {
                    log.warn("Duplicate or empty file skipped: " + imgFile.getOriginalFilename());
                }
            }
        }

        // Handle class associations
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            Set<Class> classes = request.getClassIds().stream()
                    .map(classId -> classRepository.findById(classId)
                            .orElseThrow(() -> new NoSuchElementException("Class not found: " + classId)))
                    .collect(Collectors.toSet());
            assignment.setClasses(classes);
        }

        String folderUrl = cloudinaryService.getFolderUrl(folderName);
        assignment.setImgUrl(folderUrl);
        assignment.setTeacher(teacher);

        // Save assignment to database
        return assignmentRepository.save(assignment);

    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public Assignment getAssignmentById(int assignmentId) {
        return assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Cant find assignment with id " + assignmentId));
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public AssignmentResponse getAssignmentById2(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Can't find assignment with id " + assignmentId));

        AssignmentResponse response = new AssignmentResponse();
        response.setDescription(assignment.getDescription());
        response.setContent(assignment.getContent());
        response.setCreated_date(assignment.getCreated_date());
        response.setEnd_date(assignment.getEnd_date());
        response.setClasses(assignment.getClasses().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));

        // Use folder name to fetch files
        String folderName = assignment.getDescription();
        if (folderName == null || folderName.isEmpty()) {
            throw new RuntimeException("Folder name is not set for assignment ID: " + assignmentId);
        }

        try {
            List<Map<String, Object>> resources = cloudinaryService.getFilesFromFolder(folderName);
            List<String> fileUrls = resources.stream()
                    .map(resource -> (String) resource.get("url"))
                    .collect(Collectors.toList());
            response.setFiles(fileUrls);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve files from folder: " + folderName, e);
        }

        return response;
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteAssignmentById(int assignmentId) {
        log.info("Deleting assignment with ID: {}", assignmentId);

        // Fetch assignment
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));
        log.info("Fetched assignment: {}", assignment);

        // Handle image deletion
        String imageUrl = assignment.getImgUrl();
        try {
                String folderPath = extractFolderPath(imageUrl);
                log.info("Deleting folder: {}", folderPath);
                cloudinaryService.deleteFolder(folderPath);
            } catch (Exception e) {
                log.error("Failed to delete Cloudinary folder", e);
                throw e;
        }
        // Delete assignment
        assignmentRepository.deleteById(assignmentId);
        log.info("Assignment deleted successfully.");
    }


    private String extractFolderPath(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        // Extract the part after "/upload/" and remove any trailing file names
        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid Cloudinary image URL: " + imageUrl);
        }
        String folderAndFile = parts[1];
        int lastSlashIndex = folderAndFile.lastIndexOf('/');
        String folderPath = lastSlashIndex > 0 ? folderAndFile.substring(0, lastSlashIndex) : folderAndFile;

        // Decode URL-encoded characters
        return URLDecoder.decode(folderPath, StandardCharsets.UTF_8);
    }



    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public Assignment updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));

        MultipartFile[] newFiles = request.getImgFile();
        String folderName = request.getDescription(); //Folder name = assignment's description

        if (folderName == null || folderName.isEmpty()) {
            throw new RuntimeException("Folder name is not set for assignment ID: " + assignmentId);
        }

        try {
            List<Map<String, Object>> resources = cloudinaryService.getFilesFromFolder(folderName);
            List<String> fileUrls = resources.stream()
                    .map(resource -> (String) resource.get("url"))
                    .collect(Collectors.toList());
            request.setFiles(fileUrls);
            log.info("Current Files: " + request.getFiles());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve files from folder: " + folderName, e);
        }

        // Append new files if provided
        if (newFiles != null && newFiles.length > 0) {
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
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

}

