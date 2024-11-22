package com.example.jip.services;

import com.example.jip.entity.Thread;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThreadServices {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Thread addThread(String topicName, String description, int creatorId, MultipartFile imageFile) throws IOException {
        Thread thread = new Thread();
        thread.setTopicName(topicName);
        thread.setDescription(description);
        thread.setCreatorId(creatorId);
        thread.setDateCreated(new Date(System.currentTimeMillis()));

        // Chuyển file ảnh thành mảng byte và lưu vào database
        if (imageFile != null && !imageFile.isEmpty()) {
            thread.setImage(imageFile.getBytes());
        }

        return threadRepository.save(thread);
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

    public void deleteThread(int id) {
        if (threadRepository.existsById(id)) {
            threadRepository.deleteById(id); // Xóa thread theo ID
        } else {
            throw new IllegalArgumentException("Thread not found with id: " + id);
        }
    }

}
