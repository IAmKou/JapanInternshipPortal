package com.example.jip.repository;

import com.example.jip.entity.Thread;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ThreadRepository extends CrudRepository<Thread, Integer> {
    Optional<Thread> findById(Integer id);
}

