package com.example.jip.controller;

import com.example.jip.dto.ClassDTO;
import com.example.jip.repository.AssignmentRepository;
import com.example.jip.repository.ClassRepository;
import com.example.jip.repository.ListRepository;
import com.example.jip.services.AssignmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jip.services.ClassServices;
import com.example.jip.entity.Class;

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


    @PostMapping(value = "/create", consumes = "application/json", produces = "text/plain")
    public String createClass(@RequestBody ClassDTO classDTO) {
        if (classDTO.getName() == null || classDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Class name is required");
        }
        if (classDTO.getTeacher() == null || classDTO.getTeacher().getId() == 0) {
            throw new IllegalArgumentException("Teacher ID is required");
        }
        int classCount = classRepository.countByTeacherId(classDTO.getTeacher().getId());
        if (classCount >= 3) {
            return "This teacher already has the maximum number of classes (3).";
        }


        Class savedClass = classServices.saveClassWithStudents(classDTO, classDTO.getStudentIds());
        return "Class " + savedClass.getName() + " created successfully";
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

    @GetMapping("/getByTid")
    public List<ClassDTO> getClassByTid(@RequestParam int teacherId) {
        return classRepository.findByTeacherId(teacherId).stream()
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
}
