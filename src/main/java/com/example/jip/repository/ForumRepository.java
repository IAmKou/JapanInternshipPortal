package com.example.jip.repository;

import com.example.jip.entity.Forum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ForumRepository extends CrudRepository<Forum, Integer> {
    Optional<Forum> findById(Integer id);
}

