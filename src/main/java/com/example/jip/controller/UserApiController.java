package com.example.jip.controller;

import com.example.jip.configuration.CustomUserDetail;
import com.example.jip.dto.response.UserResponse;
import com.example.jip.entity.Manager;
import com.example.jip.entity.Student;
import com.example.jip.entity.Teacher;
import com.example.jip.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RestController
public class UserApiController {
    @Autowired
    private UserServices userServices;

    @GetMapping("/user-detail")
    public ResponseEntity<?> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            int accountId = userDetails.getAccountId();
            int role = userDetails.getRoleId();

            if (role == 2) {
                Optional<Student> studentOpt = userServices.getStudentByAccountId(accountId);
                return studentOpt.map(student -> ResponseEntity.ok(new UserResponse("Student", student)))
                        .orElseGet(() -> ResponseEntity.status(404).body(new UserResponse("Error", "Student not found")));
            } else if (role == 3) {
                Optional<Teacher> teacherOpt = userServices.getTeacherByAccountId(accountId);
                return teacherOpt.map(teacher -> ResponseEntity.ok(new UserResponse("Teacher", teacher)))
                        .orElseGet(() -> ResponseEntity.status(404).body(new UserResponse("Error", "Teacher not found")));
            } else if (role == 4) {
                Optional<Manager> managerOpt = userServices.getManagerByAccountId(accountId);
                return managerOpt.map(manager -> ResponseEntity.ok(new UserResponse("Manager", manager)))
                        .orElseGet(() -> ResponseEntity.status(404).body(new UserResponse("Error", "Manager not found")));
            } else if (role == 1) {
                return ResponseEntity.ok(new UserResponse("Admin", userDetails));
            } else {
                return ResponseEntity.status(404).body(new UserResponse("Error", "User not found"));
            }
        }
        return ResponseEntity.status(401).body(new UserResponse("Error", "User not authenticated"));
    }



}
