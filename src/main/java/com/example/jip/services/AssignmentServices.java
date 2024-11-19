package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.Assignment;

import com.example.jip.entity.Teacher;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices {

    AssignmentRepository assignmentRepository;

    TeacherRepository teacherRepository;

    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('TEACHER')")
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }


    @PreAuthorize("hasAuthority('TEACHER')")
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
            for (MultipartFile imgFile : imgFiles) {
                if (!imgFile.isEmpty()) {
                    cloudinaryService.uploadFileToFolder(imgFile, folderName);
                }
            }
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
    public void deleteAssignmentById(int assignmentId) {
        if (assignmentRepository.findById(assignmentId).isPresent()) {
            assignmentRepository.deleteById(assignmentId);
        } else {
            throw new RuntimeException("Cant find assignment with id " + assignmentId);
        }
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public Assignment updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));

        MultipartFile[] newFiles = request.getImgFile();
        String folderName = request.getDescription();

        // Append new files if provided
        if (newFiles != null && newFiles.length > 0) {
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    CloudinaryResponse response = cloudinaryService.uploadFileToFolder(file, folderName);
                    String existingUrls = assignment.getImgUrl(); // Assuming imgUrl stores all file URLs as a comma-separated string
                    if (existingUrls == null || existingUrls.isEmpty()) {
                        assignment.setImgUrl(response.getUrl());
                    } else {
                        assignment.setImgUrl(existingUrls + "," + response.getUrl());
                    }
                }
            }
        }

        assignment.setEnd_date(request.getEnd_date());
        assignment.setDescription(request.getDescription());
        assignment.setContent(request.getContent());

        return assignmentRepository.save(assignment);
    }

}

