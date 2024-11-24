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

    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);
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
    public ThreadDTO findThreadById(@PathVariable("id") int id) {
        Thread thread = threadService.getThreadById(id);
        return new ThreadDTO(
                thread.getId(),
                thread.getTopicName(),
                new java.sql.Date(thread.getDateCreated().getTime()), // Explicit conversion
                thread.getDescription(),
                thread.getCreatorId(),
                thread.getImage(),
                threadService.getCreatorName(thread.getCreatorId())
        );
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

        // Log the deleted thread ID
        logger.info("Thread with ID {} has been deleted by user {}", threadId, currentUsername);

        return ResponseEntity.ok("Thread deleted successfully");
    }

//    @PutMapping("/edit/{threadId}")
//    public ResponseEntity<String> editThread(@PathVariable int threadId,
//                                             @RequestParam String topicName,
//                                             @RequestParam String description,
//                                             @RequestParam(required = false) MultipartFile imageFile,
//                                             Authentication authentication) {
//        // Retrieve the thread by its ID
//        Thread thread = threadService.getThreadById(threadId);
//
//        if (thread == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
//        }
//
//        // Check if the current user is authorized to edit the thread
//        String currentUsername = authentication.getName();
//        if (!threadService.isCreator(currentUsername, thread.getCreatorId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to edit this thread.");
//        }
//
//        // Proceed to edit the thread
//        thread.setTopicName(topicName);
//        thread.setDescription(description);
//
//        if (imageFile != null && !imageFile.isEmpty()) {
//            // Save the new image file (you can add logic to handle image saving)
//            thread.setImage(imageFile.getOriginalFilename()); // Just an example; you should handle the file saving and path management.
//        }
//
//        threadService.updateThread(thread); // Call your service to update the thread in the database
//
//        // Log the edited thread ID
//        logger.info("Thread with ID {} has been edited by user {}", threadId, currentUsername);
//
//        return ResponseEntity.ok("Thread updated successfully");
//    }
}
