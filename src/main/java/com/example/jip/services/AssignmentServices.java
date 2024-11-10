package com.example.jip.services;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.AssignmentResponse;
import com.example.jip.entity.Assignment;

import com.example.jip.mapper.AssignmentMapper;
import com.example.jip.repository.AssignmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AssignmentServices {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    AssignmentMapper assignmentMapper;
    public List<AssignmentResponse> getAllAssignments(){
        return assignmentRepository.findAll().stream()
                .map(assignmentMapper::toAssignmentResponse).toList();
    }

    public AssignmentResponse createAssignment(AssignmentCreationRequest request){

        Assignment assignment = assignmentMapper.toAssignment(request);

        //Save assignment to database
        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(assignment));


    }

    public Assignment getAssignmentById(int assignmentId){
        return assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Cant find assignment with id " + assignmentId));
    }

    public void deleteAssignmentById(int assignmentId){
        if (assignmentRepository.findById(assignmentId).isPresent()){
           assignmentRepository.deleteById(assignmentId);
        } else {
            throw new RuntimeException("Cant find assignment with id " + assignmentId);
        }
    }


    public AssignmentResponse updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));
            assignmentMapper.updateAssignemt(assignment, request);

            return assignmentMapper.toAssignmentResponse(assignmentRepository.save(assignment));

    }
}
