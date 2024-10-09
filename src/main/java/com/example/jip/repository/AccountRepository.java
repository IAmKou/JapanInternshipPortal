package com.example.jip.repository;
import com.example.jip.entity.Account;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    @Query("SELECT a FROM Account a WHERE a.username = ?1 AND a.password = ?2")
    List<Account> findByUsernameAndPassword(String username, String password);

}
