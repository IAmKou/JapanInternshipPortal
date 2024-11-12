package com.example.jip.services;

import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.repository.ManagerRepository;
import com.example.jip.repository.StudentRepository;
import com.example.jip.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ManagerRepository managerRepository;

    public Optional<Student> getStudentByAccountId(int accountId) {
        return studentRepository.findByAccount_id(accountId);
    }
    public Optional<Teacher> getTeacherByAccountId(int accountId) {
        return teacherRepository.findByAccount_id(accountId);
    }
    public Optional<Manager> getManagerByAccountId(int accountId) {
        return managerRepository.findByAccount_id(accountId);
    }


}
