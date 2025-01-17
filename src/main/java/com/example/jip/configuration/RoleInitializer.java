package com.example.jip.configuration;

import com.example.jip.entity.Role;
import com.example.jip.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("MANAGER");
        createRoleIfNotFound("STUDENT");
        createRoleIfNotFound("TEACHER");
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
