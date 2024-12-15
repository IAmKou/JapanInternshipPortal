package com.example.jip;

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
    void testInvalidPassword() {
        String username = "admin"; // correct username test object
        String password = "acb123"; // incorrect password test object
        String encodedPassword = "123456"; // actual encoded password stored in the database

        // Creating an Account Object
        Account account = new Account();
        account.setId(1);
        account.setUsername(username);
        account.setPassword(encodedPassword);

        //mock the username "admin" is found in the database
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        //password va encodedPassword not matched
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        //throw exception and check log message
        Exception exception = assertThrows(RuntimeException.class, () ->
                accountServices.authenticateAccount(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testInvalidUsername() {
        String username = "admin1"; // incorrect username test object
        String password = "123456"; // correct password test object

        //return empty object cause no username found
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        //throw exception and check log message
        Exception exception = assertThrows(RuntimeException.class, () ->
                accountServices.authenticateAccount(username, password));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testSuccessfulLogin() {
        String username = "admin"; // correct username test object
        String password = "123456"; // correct password test object
        String encodedPassword = "123456"; // actual encoded password stored in the database

        // Creating an Account Object
        Account account = new Account();
        account.setId(1);
        account.setUsername(username);
        account.setPassword(encodedPassword);

        // Mock the username "admin" is found in the database
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        // password and encodedPassword match
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Call the authenticateAccount method and check that no exception is thrown
        Account result = accountServices.authenticateAccount(username, password);

        // Check that the result is the correct Account object
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
    }
}
