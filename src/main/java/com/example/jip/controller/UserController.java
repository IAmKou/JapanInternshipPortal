// UserController.java
package com.example.jip.controller;

import com.example.jip.dto.AccountDTO;
import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AccountServices accountServices;

    @GetMapping
    public List<AccountDTO> getAllUsers() {
        return accountServices.getAllAccountDTOs();
    }

    @PutMapping("/{id}")
    public AccountDTO updateUser(@PathVariable int id, @RequestBody AccountDTO accountDTO) {
        return accountServices.updateAccount(id, accountDTO);
    }

}
