package com.example.jip.controller;

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
@RequestMapping("teacher/assignment")
public class AssignmentController {
    @Autowired
    AssignmentServices assignmentServices;

    @GetMapping("/list")
    public RedirectView getListAssignments(){
        assignmentServices.getAllAssignments();
        return new RedirectView("/list-assignment.html");
    }

    @PostMapping("/add")
    public RedirectView createAssignment(@RequestParam Date end_date,
                                         @RequestParam(required = false) String description,
                                         @RequestParam int teacher_id,
                                         @RequestParam(required = false) String img,
                                         @RequestParam int class_id){

        LocalDate localDate = LocalDate.now();
        Date created_Date = Date.valueOf(localDate);

        assignmentServices.createAssignment(created_Date, end_date, description, teacher_id, img, class_id);
        return new RedirectView("/list-assignment.html");
    }


    @DeleteMapping("/delete/{assignment_id}")
    public RedirectView deleteAssignment(@PathVariable int assignment_id){
        assignmentServices.deleteAssignmentById(assignment_id);
        return new RedirectView("/list-assignment.html");
    }

    @PutMapping("/update/{assignment_id}")
    public RedirectView updateAssignment(@PathVariable int assignment_id,
                                         @RequestParam Date end_date,
                                         @RequestParam(required = false) String description,
                                         @RequestParam(required = false) String img){

        assignmentServices.updateAssignment(assignment_id, end_date, description, img);
        return new RedirectView("/list-assignment.html");
    }




}
