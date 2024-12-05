package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.dto.request.studentAssignment.StudentAssignmentGradeRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.assignmentClass.AssignmentClassResponse;
import com.example.jip.dto.response.studentAssignment.StudentAssignmentResponse;
import com.example.jip.entity.AssignmentClass;
import com.example.jip.entity.Teacher;
import com.example.jip.exception.NotFoundException;
import com.example.jip.repository.AssignmentClassRepository;
import com.example.jip.repository.ClassRepository;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

    AssignmentServices assignmentServices;

    TeacherRepository teacherRepository;

    ClassRepository classRepository;

    AssignmentClassRepository assignmentClassRepository;

    @GetMapping("/list")
    public ResponseEntity<List<AssignmentResponse>> getAllAssignments(@RequestParam("teacherId") int teacherId) {
        try {
            List<AssignmentResponse> assignments = assignmentServices.getAllAssignmentByTeacherId(teacherId);
            return ResponseEntity.ok(assignments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return a 404 if assignment not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAssignment(@ModelAttribute AssignmentCreationRequest request,
                                              @RequestParam("teacher_id") int teacherId) {
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

    @GetMapping("/getCByTid")
    public List<ClassDTO> getClassByTid(@RequestParam("teacherId") Integer teacherId) {

        Optional<Teacher> teacherOpt = teacherRepository.findByAccount_id(teacherId);

        return classRepository.findByTeacher_Id(teacherOpt.get().getId()).stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getCByTid2")
    public List<ClassDTO> getClassByTid2(@RequestParam("teacherId") Integer teacherId) {

        return classRepository.findByTeacher_Id(teacherId).stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/getCByAid")
    public List<AssignmentClassResponse> getClassByAssignmentId(@RequestParam("assignmentId") Integer assignmentId) {

        return assignmentClassRepository.findAllByAssignmentId(assignmentId).stream()
                .map(assignmentClass -> {
                    AssignmentClassResponse response = new AssignmentClassResponse();
                    response.setAssignmentId(assignmentClass.getAssignment().getId());
                    response.setClassName(assignmentClass.getClas().getName());
                    response.setClassId(assignmentClass.getClas().getId());
                    log.info("Received response: " + response);
                    return response;
                })
                .collect(Collectors.toList());
    }



    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAssignment(@RequestParam("assignmentId") int assignmentId) {
        try {
            log.info("Deleting assignment with ID: {}", assignmentId);
            assignmentServices.deleteAssignmentById(assignmentId);
            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        } catch (RuntimeException e) {
            log.error("Error deleting assignment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Assignment not found with ID: " + assignmentId); // Return error message for debugging
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


