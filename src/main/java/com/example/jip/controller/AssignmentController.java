package com.example.jip.controller;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.entity.Assignment;
import com.example.jip.services.AssignmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {
    @Autowired
    AssignmentServices assignmentServices;

    @GetMapping("/list")
    public RedirectView getListAssignments(){
        assignmentServices.getAllAssignments();
        return new RedirectView("/list-assignment.html");
    }

    @PostMapping("/create")
    public RedirectView createAssignment(@RequestBody AssignmentCreationRequest request){

        assignmentServices.createAssignment(request);
        return new RedirectView("/list-assignment.html");
    }


    @DeleteMapping("/delete/{assignment_id}")
    public RedirectView deleteAssignment(@PathVariable int assignment_id){
        assignmentServices.deleteAssignmentById(assignment_id);
        return new RedirectView("/list-assignment.html");
    }

    @PutMapping("/update/{assignment_id}")
    public RedirectView updateAssignment(@PathVariable int assignment_id,
                                         @RequestBody AssignmentUpdateRequest request){
        assignmentServices.updateAssignment(assignment_id, request);
        return new RedirectView("/list-assignment.html");
    }






}
