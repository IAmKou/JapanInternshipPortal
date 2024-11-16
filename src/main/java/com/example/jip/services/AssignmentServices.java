package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.entity.Assignment;

import com.example.jip.entity.Teacher;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.TeacherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices {

    AssignmentRepository assignmentRepository;

    TeacherRepository teacherRepository;

    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('TEACHER')")
    public List<Assignment> getAllAssignments(){
        return assignmentRepository.findAll();
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    public Assignment createAssignment(AssignmentCreationRequest request){
            Teacher teacher = teacherRepository.findById(request.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher ID not found : " + request.getTeacher().getId()));
//            FileUploadUtil.assertAllowed(request.getImgFile(), FileUploadUtil.IMAGE_PATTERN);
           // Upload the file and get the response with URL
            final CloudinaryResponse response = cloudinaryService.uploadFile(request.getImgFile());
            // Convert the request to an Assignment entity
            Assignment assignment = new Assignment();
            // Set the image URL in the Assignment entity (not the file object)
            assignment.setCreated_date(request.getCreated_date());
            assignment.setEnd_date(request.getEnd_date());
            assignment.setDescription(request.getDescription());
            assignment.setContent(request.getContent());
            assignment.setImg(response.getUrl());
            assignment.setTeacher(teacher);

        // Save assignment to database
        return assignmentRepository.save(assignment);

    }



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
    public Assignment updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));

            final CloudinaryResponse response = cloudinaryService.uploadFile(request.getImgFile());

            assignment.setEnd_date(request.getEnd_date());
            assignment.setDescription(request.getDescription());
            assignment.setContent(request.getContent());
            assignment.setImg(request.getImg());
            assignment.setImg(response.getUrl());

            return assignmentRepository.save(assignment);

    }
}
