package com.example;

import com.example.jip.dto.AccountDTO;
import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServicesTest {

    @InjectMocks
    private AccountServices accountServices;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateAccount_Success() {
        String username = "testUser";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        Role role = new Role();
        role.setName("USER");

        Account account = new Account();
        account.setId(1);
        account.setUsername(username);
        account.setPassword(encodedPassword);
        account.setRole(role);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        AccountDTO accountDTO = accountServices.authenticateAccount(username, rawPassword);

        assertNotNull(accountDTO);
        assertEquals(username, accountDTO.getUsername());
        assertEquals("USER", accountDTO.getRoleName());

        verify(accountRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void testAuthenticateAccount_Failure_InvalidPassword() {
        String username = "testUser";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        Account account = new Account();
        account.setId(1);
        account.setUsername(username);
        account.setPassword(encodedPassword);

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                accountServices.authenticateAccount(username, rawPassword));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(accountRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void testAuthenticateAccount_Failure_InvalidUsername() {
        String username = "nonexistentUser";
        String rawPassword = "password123";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                accountServices.authenticateAccount(username, rawPassword));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(accountRepository).findByUsername(username);
        verifyNoInteractions(passwordEncoder);
    }
}
