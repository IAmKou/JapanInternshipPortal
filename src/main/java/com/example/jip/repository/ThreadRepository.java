package com.example.jip.repository;

import com.example.jip.entity.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Integer> {
    // Custom query to fetch threads with pagination
    @Query("SELECT t FROM Thread t ORDER BY t.dateCreated DESC") // Example custom query
    Page<Thread> getThreads(Pageable pageable);
}

