package com.example.jip.services;

import com.example.jip.entity.Manager;
import com.example.jip.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerServices {
    @Autowired
    private ManagerRepository managerRepository;

    public Manager createManager(Manager manager) {
        return managerRepository.save(manager);
    }

}
