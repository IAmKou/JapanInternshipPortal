package com.example.jip.repository;

import com.example.jip.entity.Listt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<Listt, Integer> {
}
