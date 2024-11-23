package com.example.jip.controller;

import com.example.jip.services.ManagerServices;
import com.example.jip.services.TeacherServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/tm")
public class TMController {

    @Autowired
    TeacherServices teacherServices;

    @Autowired
    ManagerServices managerServices;

    @PostMapping("/save")
    public RedirectView saveTM(
            @RequestParam String fullname
            , @RequestParam String jname
            , @RequestParam String gender
            , @RequestParam String email
            , @RequestParam String phoneNumber
            , @RequestParam(required = false) String img
            , @RequestParam int account_id
            , @RequestParam int role
            , RedirectAttributes redirectAttributes) {

            if (role == 3) {
                teacherServices.createTeacher(fullname, jname, email, phoneNumber, gender, img, account_id);
                redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
                return new RedirectView("/account-settings.html");
            } else {
                managerServices.createManager(fullname, jname, email, phoneNumber, gender, img, account_id);
                redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
                return new RedirectView("/account-settings.html");
            }
        }
    }
