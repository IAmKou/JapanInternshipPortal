package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.assignment.FileDeleteRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.dto.response.assignmentClass.AssignmentClassResponse;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.AssignmentClassRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.TeacherRepository;
import com.example.jip.services.AssignmentServices;
import com.example.jip.services.NotificationServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

    AssignmentServices assignmentServices;

    @GetMapping("/list")
    public ResponseEntity<List<AssignmentResponse>> getAllAssignments(@RequestParam("teacherId") int teacherId) {
        List<AssignmentResponse> assignments = assignmentServices.getAllAssignmentByTeacherId(teacherId);

        if (assignments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/list-assignment")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsForStudent(@RequestParam("studentId") int studentId) {
        try {
            List<AssignmentResponse> response = assignmentServices.getAssignmentsForStudent(studentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching unsubmitted assignments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAssignment(@ModelAttribute AssignmentCreationRequest request,
                                              @RequestParam("teacher_id") int teacherId) {
        try {
            log.info("Received request: " + request);
            log.info("Received classIds: " + request.getClassIds());

            assignmentServices.createAssignment(request, teacherId);

            log.info("Assignment create successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during assignment creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }

    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkIfDescriptionExists(@RequestParam("description") String description, @RequestParam("teacherId") int teacherId) {
        log.info("teacherId: {}", teacherId);

        boolean exists = assignmentServices.descriptionExists(description, teacherId);
        return ResponseEntity.ok(exists);
    }

    

    @GetMapping("/detailByAS/{studentAssignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentByStudentAssignmentId(
            @PathVariable int studentAssignmentId) {
        try {
            AssignmentResponse response = assignmentServices.getAssignmentByStudentAssignmentId(studentAssignmentId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Return 404 if StudentAssignment or Assignment is not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Return 500 for unexpected errors
        }
    }

    @GetMapping("/detailByC/{classId}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentByClassId(
            @PathVariable int classId) {
        try {
            List<AssignmentResponse> response = assignmentServices.getAssignmentByClassId(classId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Return 404 if  Assignment is not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Return 500 for unexpected errors
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
        public ResponseEntity<?> updateAssignment(@PathVariable("assignment_id") int assignmentId,
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
                assignmentServices.updateAssignment(assignmentId, request);
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


