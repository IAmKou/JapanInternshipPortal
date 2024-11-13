package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.AssignmentResponse;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.Assignment;

import com.example.jip.exception.NotFoundException;
import com.example.jip.mapper.AssignmentMapper;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.util.FileUploadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices {

    AssignmentRepository assignmentRepository;

    AssignmentMapper assignmentMapper;

    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('TEACHER')")
    public List<AssignmentResponse> getAllAssignments(){
        return assignmentRepository.findAll().stream()
                .map(assignmentMapper::toAssignmentResponse).toList();
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public AssignmentResponse createAssignment(AssignmentCreationRequest request){

        // Convert the request to an Assignment entity
        Assignment assignment = assignmentMapper.toAssignment(request);

        assignment.setCreated_date(new Date()); // Set the current date and time

        // Check if file is provided and validate
        if (request.getImgFile() != null && !request.getImgFile().isEmpty()) {
            FileUploadUtil.assertAllowed(request.getImgFile(), FileUploadUtil.IMAGE_PATTERN);

            // Upload the file and get the response with URL
            final String fileName = FileUploadUtil.getFileName(request.getImgFile().getOriginalFilename());
            final CloudinaryResponse response = this.cloudinaryService.uploadFile(request.getImgFile(), fileName);

            // Set the image URL in the Assignment entity (not the file object)
            assignment.setImg(response.getUrl());
        }
        // Save assignment to database
        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(assignment));

    }

    @Transactional
    public void uploadImage(int id, final MultipartFile file) {
        final Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));


        assignmentRepository.save(assignment);
    }

//    @Transactional
//    public void uploadImage(final Integer id, final MultipartFile file) {
//        final Assignment assignment = this.assignmentRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Product not found"));
//        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
//        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
//        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
//        product.setImageUrl(response.getUrl());
//        product.setCloudinaryImageId(response.getPublicId());
//        this.repository.save(product);
//    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public Assignment getAssignmentById(int assignmentId){
        return assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Cant find assignment with id " + assignmentId));
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteAssignmentById(int assignmentId){
        if (assignmentRepository.findById(assignmentId).isPresent()){
           assignmentRepository.deleteById(assignmentId);
        } else {
            throw new RuntimeException("Cant find assignment with id " + assignmentId);
        }
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    public AssignmentResponse updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));
            assignmentMapper.updateAssigment(assignment, request);

            return assignmentMapper.toAssignmentResponse(assignmentRepository.save(assignment));

    }
}
