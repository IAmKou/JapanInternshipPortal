package com.example.jip.services;

import com.example.jip.dto.request.assignment.AssignmentCreationRequest;
import com.example.jip.dto.request.assignment.AssignmentUpdateRequest;
import com.example.jip.dto.request.FileDeleteRequest;
import com.example.jip.dto.response.assignment.AssignmentResponse;
import com.example.jip.entity.*;

import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServices extends AssignmentCreationRequest {

    AssignmentRepository assignmentRepository;

    TeacherRepository teacherRepository;



    ClassRepository classRepository;

    AssignmentClassRepository assignmentClassRepository;

    AssignmentStudentRepository assignmentStudentRepository;

    StudentAssignmentRepository studentAssignmentRepository;
    private final NotificationServices notificationServices;

    S3Service s3Service;


    @PreAuthorize("hasAuthority('TEACHER')")
    public List<AssignmentResponse> getAllAssignmentByTeacherId(int teacherId) {
        return assignmentRepository.findAssignmentsByTeacherId(teacherId).stream()
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
                    response.setDescription(assignment.getDescription());
                    response.setContent(assignment.getContent());
                    response.setFolder(assignment.getImgUrl());
                    response.setClasses(
                            assignment.getClasses().stream()
                                    .map(Class::getName)
                                    .collect(Collectors.toList())
                    );
                    return response;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<AssignmentResponse> getAssignmentsForStudent(int studentId) {
        log.info("Fetching assignments for student ID: {}", studentId);
        List<Assignment> allAssignments = assignmentRepository.findAssignmentsByStudentId(studentId);
        List<Integer> submittedAssignmentIds = studentAssignmentRepository.findSubmittedAssignmentIdsByStudentId(studentId);

        return allAssignments.stream()
                .filter(assignment -> !submittedAssignmentIds.contains(assignment.getId()))
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setDescription(assignment.getDescription());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
                    return response;
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public Assignment createAssignment(AssignmentCreationRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher ID not found: " + request.getTeacher().getId()));
        if (request.getCreated_date().after(request.getEnd_date())) {
            throw new IllegalArgumentException("Created date cannot be after end date.");
        }

        if (request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }

        if (request.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty.");
        }

        // Create Assignment entity
        Assignment assignment = new Assignment();
        assignment.setCreated_date(request.getCreated_date());
        assignment.setEnd_date(request.getEnd_date());

        assignment.setDescription(request.getDescription());
        assignment.setContent(request.getContent());
        assignment.setTeacher(teacher);

        // Sanitize and create folder name
        String folderName = sanitizeFolderName("assignments/" + request.getDescription());
        assignment.setImgUrl(folderName); // Set folder URL
        if (request.getImgFile() != null) {
            MultipartFile[] imgFiles = request.getImgFile();
            for (int i = 0; i < request.getImgFile().length; i++) {
                log.info("Uploading file: " + request.getImgFile()[i].getOriginalFilename());
            }
            uploadFilesToFolder(imgFiles, folderName);

        } else {
            log.warn("No files provided in the request.");
        }

        // Save assignment
        Assignment savedAssignment = assignmentRepository.save(assignment);

        // Assign the assignment to classes and students
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                Optional<Class> clasOpt = classRepository.findById(classId);
                if (clasOpt.isPresent()) {
                    Class clas = clasOpt.get();

                    // Link assignment to class
                    assignmentClassRepository.save(new AssignmentClass(savedAssignment, clas));

                    // Link assignment to all students in the class
                    for (Listt listEntry : clas.getClassLists()) {
                        Student student = listEntry.getStudent();
                        assignmentStudentRepository.save(new AssignmentStudent(savedAssignment, student));
                        notificationServices.createAutoNotificationForAssignment("New assignment have been created", teacher.getId(), student.getId());
                    }
                } else {
                    log.warn("Class with ID {} not found", classId);
                }
            }
        } else {
            log.warn("No class IDs provided.");
        }

        // Save the assignment
        return assignmentRepository.save(assignment);
    }

    public boolean descriptionExists(String description) {
        return assignmentRepository.existsByDescription(description);
    }


    public AssignmentResponse getAssignmentById(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + assignmentId));

        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setDescription(assignment.getDescription());
        response.setContent(assignment.getContent());
        response.setCreated_date(assignment.getCreated_date());
        response.setEnd_date(assignment.getEnd_date());
        response.setClasses(assignment.getClasses().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));

        // Retrieve file URLs from Cloudinary
        String folderName = assignment.getImgUrl();

        try {
            if(!folderName.isEmpty()){
                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                if (fileUrls.isEmpty()) {
                    log.warn("No files found for assignment with ID: {}", assignmentId);
                }
                response.setFiles(fileUrls);
            }
        } catch (NullPointerException ne){
            log.error("No file found");
        } catch (Exception e) {
            log.error("Error retrieving files for assignment with ID: {}", assignmentId, e);
            response.setFiles(Collections.emptyList());
        }

        return response;
    }

    public AssignmentResponse getAssignmentByStudentAssignmentId(int studentAssignmentId) {
        // Find the StudentAssignment by ID
        StudentAssignment studentAssignment = studentAssignmentRepository.findById(studentAssignmentId)
                .orElseThrow(() -> new NoSuchElementException("StudentAssignment not found"));

        // Get the related Assignment
        Assignment assignment = studentAssignment.getAssignment();

        if (assignment == null) {
            throw new NoSuchElementException("Assignment not found for this StudentAssignment");
        }

        // Map Assignment to AssignmentResponse
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setDescription(assignment.getDescription());
        response.setContent(assignment.getContent());
        response.setCreated_date(assignment.getCreated_date());
        response.setEnd_date(assignment.getEnd_date());
        response.setClasses(assignment.getClasses().stream()
                .map(Class::getName)
                .collect(Collectors.toList()));
        String folderName = assignment.getImgUrl();
        try {
            if(!folderName.isEmpty()){
                List<String> fileUrls = s3Service.listFilesInFolder(folderName);
                if (fileUrls.isEmpty()) {
                    log.warn("No files found for assignment with ID: {}", response.getId());
                }
                response.setFiles(fileUrls);
            }

        } catch (NullPointerException ne){
            log.error("No file found");
        }
        catch (Exception e) {
            log.error("Error retrieving files for assignment with ID: {}", response.getId(), e);
            response.setFiles(Collections.emptyList());
        }
        return response;
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public void deleteAssignmentById(int assignmentId) {
        // Find assignment or throw exception if not found
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + assignmentId));

        // Delete related rows in dependent tables
        assignmentStudentRepository.deleteByAssignmentId(assignmentId);
        assignmentClassRepository.deleteByAssignmentId(assignmentId);
        studentAssignmentRepository.deleteByAssignmentId(assignmentId);

        // Delete associated cloud resources if applicable
        if(assignment.getImgUrl() != null) {
            String folderPath = sanitizeFolderName(assignment.getImgUrl());
            s3Service.deleteFolder(folderPath);
        }

        // Finally, delete the assignment
        assignmentRepository.delete(assignment);
    }

    private void uploadFilesToFolder(MultipartFile[] files, String folderName) {
        Set<String> uploadedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty() && uploadedFiles.add(file.getOriginalFilename())) {
                try {
                    String sanitizedFolderName = sanitizeFolderName(folderName);
                    s3Service.uploadFile(file, sanitizedFolderName, file.getOriginalFilename());
                } catch (Exception e) {
                    throw new RuntimeException("Error uploading file: " + file.getOriginalFilename(), e);
                }
            }
        }
    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim().replace(" ", "_");
    }


    @PreAuthorize("hasAuthority('TEACHER')")
    @Transactional
    public Assignment updateAssignment(int assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment id not found!"));

        if (request.getImgFile() != null) {
            MultipartFile[] newFiles = request.getImgFile();
            for (int i = 0; i < request.getImgFile().length; i++) {
                log.info("Uploading file: " + request.getImgFile()[i].getOriginalFilename());
            }
            if (newFiles.length > 0) {
                String folderName = sanitizeFolderName("assignments/" + request.getDescription());
                if (folderName.isEmpty()) {
                    throw new RuntimeException("Folder name is not set for assignment ID: " + assignmentId);
                }
                uploadFilesToFolder(newFiles, folderName);
                assignment.setImgUrl(folderName); // Set folder URL
            }
        } else {
            log.warn("No files provided in the request.");
        }

        // **Clear existing class associations**
        assignmentClassRepository.deleteByAssignmentId(assignment.getId());

        // Add new class associations
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                // Find the class
                Class clas = classRepository.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found: " + classId));

                // Link assignment to class
                assignmentClassRepository.save(new AssignmentClass(assignment, clas));

                // Link assignment to all students in the class
                for (Listt listEntry : clas.getClassLists()) {
                    Student student = listEntry.getStudent();
                    assignmentStudentRepository.save(new AssignmentStudent(assignment, student));
                }
            }
        }
        // Update other fields
        if (request.getEnd_date() != null) {
            assignment.setEnd_date(request.getEnd_date());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            assignment.setContent(request.getContent());
        }
        return assignmentRepository.save(assignment);
    }



    public void deleteFile(FileDeleteRequest request){
    // Sanitize folder name and delete the file
        String folderName = sanitizeFolderName("assignments/" + assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new NoSuchElementException("Assignment not found!"))
                .getDescription());

        s3Service.deleteFile(request.getFileUrl(), folderName);
    }


    public List<AssignmentResponse> getAssignmentByClassId(int classId) {
        // Find the StudentAssignment by ID
       Class clas = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));

        // Get the related Assignment
        Set<Assignment> assignments = clas.getAssignments();

        if (assignments == null || assignments.isEmpty()) {
            throw new NoSuchElementException("Assignment not found for this Class");
        }
        List<AssignmentResponse> assignmentResponses = assignments.stream()
                .map(assignment -> {
                    AssignmentResponse response = new AssignmentResponse();
                    response.setId(assignment.getId());
                    response.setCreated_date(assignment.getCreated_date());
                    response.setEnd_date(assignment.getEnd_date());
                    response.setDescription(assignment.getDescription());
                    response.setContent(assignment.getContent());
                    response.setFolder(assignment.getImgUrl());
                    response.setClasses(
                            assignment.getClasses().stream()
                                    .map(Class::getName)
                                    .collect(Collectors.toList())
                    );
                    return response;
                })
                .collect(Collectors.toList());

        return assignmentResponses;
    }
}

