package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.NotificationDTO;
import com.example.jip.entity.Notification;
import com.example.jip.entity.Semester;
import com.example.jip.repository.*;
import com.example.jip.services.AssignmentServices;
import com.example.jip.services.NotificationServices;
import com.example.jip.services.SemesterServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jip.services.ClassServices;
import com.example.jip.entity.Class;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassServices classServices;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private MarkReportRepository markReportRepository;


    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public String createClass(@RequestBody ClassDTO classDTO) {
        int semesterId = classDTO.getSemesterId();
        System.out.println(semesterId);
        Semester semester = semesterRepository.findById(semesterId).
                orElseThrow(() -> new NoSuchElementException("semester not found"));
        Date st = semester.getStart_time();
        Date ed = semester.getEnd_time();
        LocalDate semesterStart = st.toLocalDate();
        LocalDate semesterEnd = ed.toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (!currentDate.isBefore(semesterStart) && !currentDate.isAfter(semesterEnd)) {
            return "Cannot create a class as the semester has already started.";
        }

        if (classDTO.getName() == null || classDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Class name is required");
        }

        boolean classExists = classRepository.existsByName(classDTO.getName());
        if (classExists) {
            return "A class with the name [" + classDTO.getName() + "] already exists.";
        }

        if (classDTO.getTeacher() == null || classDTO.getTeacher().getId() == 0) {
            throw new IllegalArgumentException("Teacher ID is required");
        }


        int classCount = classRepository.countClassesByTeacherAndSemester(classDTO.getTeacher().getId(), semesterId);
        if (classCount >= 3) {
            return "This teacher is already assigned to the maximum number of classes (3) for this semester.";
        }

        try {
            Class savedClass = classServices.saveClassWithStudents(classDTO, classDTO.getStudentIds(), semesterId);
            return "Class " + savedClass.getName() + " created successfully";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); // Return failure message if student list is empty
        }
    }

   @PostMapping("/update")
   public ResponseEntity<ClassDTO> updateClass(@RequestBody ClassDTO classDTO) {
       // Update the class and handle exceptions
       try {
           ClassDTO updatedClass = classServices.updateClass(classDTO.getId(), classDTO);
           return ResponseEntity.ok(updatedClass); // Return 200 with the updated data
       } catch (NoSuchElementException ex) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if the class or teacher is not found
       } catch (Exception ex) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 for other errors
       }

   }

    @GetMapping("/get")
    public List<ClassDTO> getClasses() {
        return classRepository.findAll().stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getByTid/{teacherId}")
    public List<ClassDTO> getClassByTid(@PathVariable Integer teacherId) {
        return classRepository.findByTeacher_Id(teacherId).stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteClass(@PathVariable int id) {
        if (classRepository.existsById(id)) {
            listRepository.deleteStudentsByClassId(id);
            classRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @GetMapping("/student/{studentId}")
    public List<ClassDTO> getClassesByStudentId(@PathVariable int studentId) {
        return classServices.getClassByStudentId(studentId).stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getCById")
    public ClassDTO getClassById(@RequestParam("classId") int id) {
        return classRepository.findById(id).map(ClassDTO::new).orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }

    @GetMapping("/{classId}/getAllGrades")
    public List<MarkReportDTO> getAllGrades(@PathVariable Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("classId must not be null");
        }
        List<MarkReportDTO> markReports = listRepository.getStudentsWithMarkReportsByClassId(classId);
        return markReports;
    }

}
