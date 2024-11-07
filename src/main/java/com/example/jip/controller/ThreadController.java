package com.example.jip.controller;
import com.example.jip.services.ThreadServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/thread")
public class ThreadController {
    @Autowired
    ThreadServices threadService;

    @GetMapping("/add-thread")
    public String showAddThreadForm() {
        return "add-thread"; // File HTML để hiển thị form
    }

    @PostMapping("/add-thread")
    public String addThread(
            @RequestParam("topicName") String topicName,
            @RequestParam("description") String description,
            @RequestParam("creatorId") int creatorId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        try {
            threadService.addThread(topicName, description, creatorId, imageFile);
            model.addAttribute("message", "Thread added successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error saving thread: " + e.getMessage());
        }

        return "add-thread";
    }
}
