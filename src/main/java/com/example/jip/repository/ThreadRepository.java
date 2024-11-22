package com.example.jip.repository;

import com.example.jip.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Integer> {
    Optional<Thread> findById(Integer id);
}

