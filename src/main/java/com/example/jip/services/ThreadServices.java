package com.example.jip.services;

import com.example.jip.entity.Thread;
import com.example.jip.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class ThreadServices {
    private ThreadRepository threadRepository;

    public Thread addThread(String topicName, String description, int creatorId, MultipartFile imageFile) throws IOException {
        Thread thread = new Thread();
        thread.setTopicName(topicName);
        thread.setDescription(description);
        thread.setCreatorId(creatorId);
        thread.setDateCreated((java.sql.Date) new Date());

        // Chuyển file ảnh thành mảng byte và lưu vào database
        if (imageFile != null && !imageFile.isEmpty()) {
            thread.setImage(imageFile.getBytes());
        }

        return threadRepository.save(thread);
    }
}
