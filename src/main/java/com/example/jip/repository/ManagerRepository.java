package com.example.jip.repository;


import com.example.jip.entity.Manager;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ManagerRepository extends CrudRepository<Manager, Integer> {
    Optional<Manager> findById(Integer id);
    Optional<Manager> findByAccount_id(Integer account_id);

}
