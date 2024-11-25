package com.example.jip.controller;
import com.example.jip.configuration.SecurityConfig;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.ThreadDTO;
import com.example.jip.entity.Thread;
import com.example.jip.services.ThreadServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    @Autowired
    ThreadServices threadService;

    @GetMapping()
    public Page<ThreadDTO> showAllThread(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ThreadDTO> pageOfThreads = threadService.getAllThread(pageable)
                .map(x -> new ThreadDTO(
                        x.getId(),
                        x.getTopicName(),
                        new java.sql.Date(x.getDateCreated().getTime()), // Explicit conversion
                        x.getDescription(),
                        x.getCreatorId(),
                        x.getImage(),
                        threadService.getCreatorName(x.getCreatorId())
                ));
        return pageOfThreads; // This includes totalPages
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreadDTO> findThreadById(@PathVariable("id") int id) {
        Thread thread = threadService.getThreadById(id);
        if (thread == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ThreadDTO threadDTO = new ThreadDTO(
                thread.getId(),
                thread.getTopicName(),
                new java.sql.Date(thread.getDateCreated().getTime()),
                thread.getDescription(),
                thread.getCreatorId(),
                thread.getImage(),
                threadService.getCreatorName(thread.getCreatorId())
        );
        return ResponseEntity.ok(threadDTO);
    }

    @GetMapping("/add-thread")
    public String showAddThreadForm() {
        return "create-forum"; //Show out HTML
    }

    @PostMapping("/add-thread")
    public RedirectView addThread(
            @RequestParam("topicName") String topicName,
            @RequestParam("description") String description,
            @RequestParam("creatorId") int creatorId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        try {
            // Call the service to add a thread with the current date
            threadService.addThread(topicName, description, creatorId, imageFile);
            model.addAttribute("message", "Thread added successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error saving thread: " + e.getMessage());
        }

        return new RedirectView("/forum.html");
    }

    // Endpoint to delete a thread
    @DeleteMapping("/delete/{threadId}")
    public ResponseEntity<String> deleteThread(@PathVariable int threadId, Authentication authentication) {
        // Retrieve the thread by its ID
        Thread thread = threadService.getThreadById(threadId);

        if (thread == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
        }

        // Check if the current user is authorized to delete the thread
        String currentUsername = authentication.getName();
        if (!threadService.isCreator(currentUsername, thread.getCreatorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this thread.");
        }

        // Proceed to delete the thread
        threadService.deleteThread(threadId);

        return ResponseEntity.ok("Thread deleted successfully");
    }

    @PutMapping("/edit/{threadId}")
    public ResponseEntity<String> editThread(@PathVariable int threadId,
                                             @RequestParam String topicName,
                                             @RequestParam String description,
                                             @RequestParam(required = false) MultipartFile imageFile,
                                             Authentication authentication) {
        // Retrieve the thread by its ID
        Thread thread = threadService.getThreadById(threadId);

        if (thread == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
        }

        // Check if the current user is authorized to edit the thread
        String currentUsername = authentication.getName();
        if (!threadService.isCreator(currentUsername, thread.getCreatorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to edit this thread.");
        }

        // Update thread fields
        thread.setTopicName(topicName);
        thread.setDescription(description);

        // Process the image file if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes(); // Get binary data
                thread.setImage(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing the uploaded image.");
            }
        }

        // Save updated thread
        threadService.updateThread(thread);

        return ResponseEntity.ok("Thread updated successfully");
    }
}
