package com.example.jip.repository;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    int countByRole(Role role);
}
