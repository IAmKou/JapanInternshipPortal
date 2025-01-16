package com.example.jip.services;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.SemesterDateDTO;
import com.example.jip.dto.StudentDTO;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
import com.example.jip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassServices {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    public Class saveClassWithStudents(ClassDTO classDTO, List<Integer> studentIds, int semesterId) {
        // Check if the studentIds list is empty
        if (studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("Student list cannot be empty.");
        }

        Teacher teacher = teacherRepository.findById(classDTO.getTeacher().getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + classDTO.getTeacher().getId()));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found with ID: " + classDTO.getSemesterId()));

        Class newClass = new Class();
        newClass.setName(classDTO.getName());
        newClass.setTeacher(teacher);
        newClass.setNumber_of_student(studentIds.size());
        newClass.setSemester(semester);
        newClass.setStatus(Class.status.Inactive);

        Class savedClass = classRepository.save(newClass);

        for (Integer studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

            Listt listEntry = new Listt();
            ListId listId = new ListId(savedClass.getId(), student.getId());
            listEntry.setId(listId);
            listEntry.setClas(savedClass);
            listEntry.setStudent(student);

            listRepository.save(listEntry);
        }

        return savedClass;
    }


    public ClassDTO updateClass(int id, ClassDTO classDTO) {
        Class newClass = classRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id " + id));
        if (classDTO.getName() != null && !classDTO.getName().isEmpty()) {
            newClass.setName(classDTO.getName());
        }

        if (classDTO.getTeacher() != null) {
            Teacher newTeacher = teacherRepository.findById(classDTO.getTeacher().getId())
                    .orElseThrow(() -> new NoSuchElementException("Teacher not found with id " + classDTO.getTeacher().getId()));
            newClass.setTeacher(newTeacher);
        }

        classRepository.save(newClass);


        return new ClassDTO(newClass);
    }

    public boolean isNameExist(String name, int semesterId, int excludeClassId) {
        List<Class> clasz = classRepository.findAll();
        for (Class c : clasz) {
            // Skip the class being updated (exclude it from the check)
            if (c.getId() == excludeClassId) {
                continue;
            }
            if (c.getName().equals(name) && c.getSemester().getId() == semesterId) {
                return true;
            }
        }
        return false;
    }


    public List<Class> getClassByStudentId(int studentId) {
        return classRepository.findClassesByStudentId(studentId);
    }

    public SemesterDateDTO getSemesterDatesByClassId(int classId) {
        Object[] result = classRepository.findSemesterStartAndEndByClassId(classId);
        if (result != null && result.length == 2) {
            Date startTime = (Date) result[0];
            Date endTime = (Date) result[1];
            return new SemesterDateDTO(startTime, endTime);
        }
        return null; // Handle case where the class or semester does not exist
    }
}
