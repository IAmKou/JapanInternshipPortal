package com.example.jip.services;

import com.example.jip.entity.Assignment;

import com.example.jip.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;


@Service
public class AssignmentServices {

    @Autowired
    private AssignmentRepository assignmentRepository;

    public List<Assignment> getAllAssignments(){
        return assignmentRepository.findAll();
    }

    public Assignment createAssignment(Date date_created, Date end_date, String description, int teacher_id, String img, int class_id){

        Assignment assignment = new Assignment();
        assignment.setCreated_date(date_created);
        assignment.setEnd_date(end_date);
        assignment.setDescription(description);
        assignment.setTeacher_id(teacher_id);
        assignment.setImg(img);
        assignment.setClass_id(class_id);

        //Save assignment to database
        return assignmentRepository.save(assignment);
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


    public Assignment updateAssignment(int assignmentId, Date endDate, String description, String img) {
        Assignment existingAssignment = getAssignmentById(assignmentId);
        if (existingAssignment != null) {
            existingAssignment.setEnd_date(endDate);
            existingAssignment.setDescription(description);
            existingAssignment.setImg(img);

            return assignmentRepository.save(existingAssignment);
        } else {
            throw new RuntimeException("Cant find assignment with id " + assignmentId);
        }
    }
}
