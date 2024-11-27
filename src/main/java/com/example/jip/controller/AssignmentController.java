package com.example.jip.controller;

import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.AssignmentServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
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
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentServices.getAllAssignments();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAssignment(@ModelAttribute AssignmentCreationRequest request,
                                              @RequestParam("teacher_id") int teacherId) throws IOException {
        try {
            log.info("Received request: " + request);

            for (int i = 0; i < request.getImgFile().length; i++) {
                log.info("Received file: " + request.getImgFile()[i].getOriginalFilename());
            }
            log.info("Received classIds: " + request.getClassIds());

            Optional<Teacher> teacherOpt = teacherRepository.findByAccount_id(teacherId);
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setId(teacherOpt.get().getId());
            request.setTeacher(teacherDTO);

            assignmentServices.createAssignment(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            throw new RuntimeException("Failed to create assignment", e);
        }
    }


    @DeleteMapping("/delete/{assignment_id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable("assignment_id") int assignment_id) {
        try {
            assignmentServices.deleteAssignmentById(assignment_id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if assignment doesn't exist
        }
    }


    @GetMapping("/detail/{assignment_id}")
        public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable("assignment_id") int assignmentId) {
           log.info("Received assignmentId: " + assignmentId);
            AssignmentResponse response = assignmentServices.getAssignmentById(assignmentId);
           log.info("Files: " + response.getFiles());
               if (response != null)  {
                    return ResponseEntity.ok(response);
                 } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                 }
    }

    @PutMapping("/update/{assignment_id}")
        public ResponseEntity<?> updateAssignment(@PathVariable("assignment_id") int assignment_id,
                                                  @ModelAttribute AssignmentUpdateRequest request) {
            try {
                log.info("Received request: " + request);  // Log the incoming request for debugging


                if (request.getImgFile() != null) {
                    for (MultipartFile imgFile : request.getImgFile()) {
                        log.info("Received file: " + imgFile.getOriginalFilename()); // Log the file name
                    }
                } else {
                    log.info("No files received.");
                }

                log.info("Received classIds: " + request.getClassIds());
                assignmentServices.updateAssignment(assignment_id, request);
                return ResponseEntity.noContent().build(); // Return 204 No Content on successful update
            } catch (NoSuchElementException e) {
                log.error("Assignment not found", e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 Not Found if assignment doesn't exist
            }
        }

    @DeleteMapping("/delete-file")
    public ResponseEntity<?> deleteFile(@RequestBody FileDeleteRequest request) {
        try {
            assignmentServices.deleteFile(request);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting file: {}", request.getFileUrl(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file.");
        }
    }
}


