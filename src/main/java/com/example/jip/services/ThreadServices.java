package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Thread;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ThreadServices {

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Add a new thread
    public Thread addThread(String topicName, String description, int creatorId, MultipartFile imageFile) throws IOException {
        Thread thread = new Thread();
        thread.setTopicName(topicName);
        thread.setDescription(description);
        thread.setCreatorId(creatorId);
        thread.setDateCreated(new java.util.Date());

        // If an image file is provided, convert it to byte array and save it in the thread entity
        if (imageFile != null && !imageFile.isEmpty()) {
            thread.setImage(imageFile.getBytes());
        }

        // Save the new thread to the database
        return threadRepository.save(thread);
    }

    // Get creator's username by their ID
    public String getCreatorName(int creatorId) {
        // Using Optional to return a fallback value if account is not found
        return accountRepository.findById(creatorId)
                .map(Account::getUsername)
                .orElse("Unknown");  // Return "Unknown" if creator is not found
    }

    // Get all threads with pagination
    public Page<Thread> getAllThread(Pageable pageable) {
        return threadRepository.findAll(pageable);
    }

    // Get a specific thread by ID
    public Thread getThreadById(int id) {
        return threadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thread not found"));
    }

    // Check if the logged-in user is the creator of the thread
    public boolean isCreator(String username, int creatorId) {
        // Fetch account once to avoid redundant database calls
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare the account ID with the creator ID of the thread
        return account.getId() == creatorId;
    }

    // Delete a thread by ID
    public void deleteThread(int threadId) {
        if (!threadRepository.existsById(threadId)) {
            throw new RuntimeException("Thread not found");
        }
        threadRepository.deleteById(threadId);
    }

    // Update an existing thread
    public void updateThread(Thread thread) {
        // Ensure the thread exists before updating
        if (!threadRepository.existsById(thread.getId())) {
            throw new RuntimeException("Thread not found");
        }
        threadRepository.save(thread); // Save the updated thread in the database
    }
}
