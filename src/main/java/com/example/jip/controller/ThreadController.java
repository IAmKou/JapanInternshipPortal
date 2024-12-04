package com.example.jip.controller;

import com.example.jip.dto.ThreadDTO;
import com.example.jip.entity.Account;
import com.example.jip.entity.Thread;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.ThreadServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

@RestController
@RequestMapping("/thread")
public class ThreadController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ThreadServices threadService;

    @GetMapping("/threads")
    public Page<ThreadDTO> getAllThreads(@RequestParam("page") int page, @RequestParam("size") int size) {
        System.out.println("Received page: " + page + ", size: " + size);

        // Fetch threads
        Page<Thread> threads = threadService.getAllThread(PageRequest.of(page, size));

        // Map Thread to ThreadDTO and include creator's name
        Page<ThreadDTO> threadDTOs = threads.map(thread -> {
            String creatorName = threadService.getCreatorName(thread.getCreatorId()); // Get creator name
            return new ThreadDTO(
                    thread.getId(),
                    thread.getTopicName(),
                    new java.sql.Date(thread.getDateCreated().getTime()), // Explicit conversion to SQL Date
                    thread.getDescription(),
                    thread.getCreatorId(),
                    thread.getImage(),
                    creatorName // Add creator name
            );
        });

        return threadDTOs;
    }


    // Endpoint to get paginated list of threads
    @GetMapping
    public Page<ThreadDTO> showAllThread(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        int pageIndex = Math.max(page - 1, 0); // Ensure page is at least 0-based
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<ThreadDTO> pageOfThreads = threadService.getAllThread(pageable)
                .map(x -> new ThreadDTO(
                        x.getId(),
                        x.getTopicName(),
                        new java.sql.Date(x.getDateCreated().getTime()), // Explicit conversion to SQL Date
                        x.getDescription(),
                        x.getCreatorId(),
                        x.getImage(),

                        threadService.getCreatorName(x.getCreatorId())
                ));

        return pageOfThreads; // Returns paginated data
    }
    // Endpoint to get a specific thread by its ID
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
                threadService.getCreatorName(thread.getCreatorId()) // Assuming service provides creator name
        );

        return ResponseEntity.ok(threadDTO); // Return thread as DTO
    }

    // Show form to create a new thread
    @GetMapping("/add-thread")
    public String showAddThreadForm() {
        return "create-forum"; // This returns the view for creating a new thread
    }

    // Endpoint to add a new thread
    @PostMapping("/add-thread")
    public ResponseEntity<ThreadDTO> addThread(@RequestParam("topicName") String topicName,
                                               @RequestParam("description") String description,
                                               @RequestParam("creatorId") int creatorId,
                                               @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // Call service to add thread and return the created thread
            Thread newThread = threadService.addThread(topicName, description, creatorId, imageFile);

            // Convert the thread to ThreadDTO for a structured JSON response
            ThreadDTO threadDTO = new ThreadDTO(
                    newThread.getId(),
                    newThread.getTopicName(),
                    new java.sql.Date(newThread.getDateCreated().getTime()),
                    newThread.getDescription(),
                    newThread.getCreatorId(),
                    newThread.getImage(),
                    threadService.getCreatorName(newThread.getCreatorId())
            );

            // Return the created thread as a JSON response
            return ResponseEntity.status(HttpStatus.CREATED).body(threadDTO);
        } catch (IOException e) {
            // Log the error and return a proper error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint to delete a thread
    @DeleteMapping("/delete/{threadId}")
    public ResponseEntity<String> deleteThread(@PathVariable int threadId, Authentication authentication) {
        Thread thread = threadService.getThreadById(threadId);

        if (thread == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
        }

        String currentUsername = authentication.getName();
        if (!threadService.isCreator(currentUsername, thread.getCreatorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this thread.");
        }

        threadService.deleteThread(threadId); // Proceed to delete the thread
        return ResponseEntity.ok("Thread deleted successfully");
    }

    // Endpoint to edit a thread
    @PutMapping("/edit/{threadId}")
    public ResponseEntity<String> editThread(@PathVariable int threadId,
                                             @RequestParam String topicName,
                                             @RequestParam String description,
                                             @RequestParam(required = false) MultipartFile imageFile,
                                             Authentication authentication) {

        Thread thread = threadService.getThreadById(threadId);

        if (thread == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
        }

        String currentUsername = authentication.getName();
        if (!threadService.isCreator(currentUsername, thread.getCreatorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to edit this thread.");
        }

        // Update thread fields
        thread.setTopicName(topicName);
        thread.setDescription(description);

        // Handle the image file if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                thread.setImage(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing the uploaded image.");
            }
        }

        threadService.updateThread(thread); // Save updated thread
        return ResponseEntity.ok("Thread updated successfully");
    }
}
