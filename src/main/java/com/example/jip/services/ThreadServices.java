package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Thread;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ThreadServices {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Thread addThread(String topicName, String description, int creatorId, MultipartFile imageFile) throws IOException {
        Thread thread = new Thread();
        thread.setTopicName(topicName);
        thread.setDescription(description);
        thread.setCreatorId(creatorId);
        thread.setDateCreated(new java.util.Date());

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            thread.setImage(imageFile.getBytes());
        }

        Thread savedThread = threadRepository.save(thread); // Save the thread

        // Explicitly flush to commit
        threadRepository.flush();

        return savedThread; // Return the saved thread
    }

    public String getCreatorName(int creatorId) {
        return this.accountRepository.findById(creatorId).orElseThrow().getUsername();
    }

    public Page<Thread> getAllThread(Pageable pageable) {
        return (Page<Thread>) this.threadRepository.findAll(pageable);
    }

    public Thread getThreadById(int id) {
        return threadRepository.findById(id).orElseThrow();
    }

    public boolean isCreator(String username, int creatorId) {
        // Fetch the account by username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare the account ID with the creatorId of the thread
        return account.getId() == creatorId;
    }

    public void deleteThread(int threadId) {
        threadRepository.deleteById(threadId);
    }

    public void updateThread(Thread thread) {
        threadRepository.save(thread); // Save the updated thread to the database
    }
}
