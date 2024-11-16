package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.AssignmentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

    AssignmentServices assignmentServices;

    TeacherRepository teacherRepository;


    @GetMapping("/list")
    public List<Assignment> getAllAssignments() {
        return assignmentServices.getAllAssignments();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView createAssignment(@ModelAttribute AssignmentCreationRequest request,
                                         @RequestParam("teacher_id") int teacherId,
                                         @RequestParam("imgFile") MultipartFile imgFile) throws IOException {
        try {
            log.info("Received request: " + request);  // Log the incoming request for debugging
            request.setImgFile(imgFile);  // Set imgFile to the request object

            Optional<Teacher> teacherOpt = teacherRepository.findByAccount_id(teacherId);
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacherOpt.get().getId());
            request.setTeacher(teacherDTO);

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
