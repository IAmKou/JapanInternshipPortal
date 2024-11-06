package com.example.jip.services;

import com.example.jip.entity.Forum;
import com.example.jip.entity.List;
import com.example.jip.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForumServices {

    @Autowired
    private ForumRepository forumRepository;

    public Forum createForum(Forum forum) {
        return forumRepository.save(forum);
    }

    public Forum readForum(int Id) {
        Optional<Forum> forumOptional = forumRepository.findById(Id);

        if (!forumOptional.isPresent()) {
            throw new IllegalArgumentException("No student found with id: " + Id);
        }

        return forumOptional.get();
    }
}
