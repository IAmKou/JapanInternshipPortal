package com.example.jip.controller;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.entity.Assignment;
import com.example.jip.services.AssignmentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {
    @Autowired
    AssignmentServices assignmentServices;

    @GetMapping("/list")
    public RedirectView getListAssignments(){
        assignmentServices.getAllAssignments();
        return new RedirectView("/list-assignment.html");
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView createAssignment(@ModelAttribute AssignmentCreationRequest request) throws IOException {
        try {
            log.info("Received request: " + request);  // Log the incoming request for debugging
            assignmentServices.createAssignment(request);
            return new RedirectView("/add-assignment.html");
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            throw new RuntimeException("Failed to create assignment", e);
        }
    }


    @DeleteMapping("/delete/{assignment_id}")
    public RedirectView deleteAssignment(@PathVariable("assignment_id") int assignment_id){
        assignmentServices.deleteAssignmentById(assignment_id);
        return new RedirectView("/list-assignment.html");
    }

    @PutMapping("/update/{assignment_id}")
    public RedirectView updateAssignment(@PathVariable("assignment_id") int assignment_id,
                                         @RequestBody AssignmentUpdateRequest request){
        assignmentServices.updateAssignment(assignment_id, request);
        return new RedirectView("/list-assignment.html");
    }

}
