package com.example.jip.controller;

import com.example.jip.dto.AccountDTO;
import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import com.example.jip.services.EmailServices;
import com.example.jip.services.VerificationCodeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AccountServices accountServices;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private VerificationCodeServices verificationCodeService;

    @GetMapping
    public List<AccountDTO> getAllUsers() {
        return accountServices.getAllAccountDTOs();
    }

    @PostMapping("/{id}")
    public AccountDTO updateUser(@PathVariable int id,
                                 @ModelAttribute AccountDTO accountDTO) {
        return accountServices.updateAccount(id, accountDTO);
    }

    @GetMapping("/check")
    public ModelAndView checkUser(@RequestParam(name = "username", required = true) String username) {
        // Check if the username exists
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return new ModelAndView("redirect:/forgot-password.html").addObject("message", "account not found.");
        }

        // Retrieve account and send verification email
        Account account = accountOpt.get();
        AccountDTO accountDTO = new AccountDTO(account);
        String email = accountDTO.getEmail();

        System.out.println("email: " + email);
        System.out.println("username: " + username);

        String verifyCode = emailServices.sendVerificationCode(email);
        System.out.println("verifyCode: " + verifyCode);

        if (verifyCode == null) {
            return new ModelAndView("redirect:/forgot-password.html").addObject("message", "Failed to send verification code.");
        }

        // Save the verification code and username
        verificationCodeService.setCurrentUsername(username);
        verificationCodeService.saveVerificationCode(username, verifyCode);

        return new ModelAndView("redirect:/verify-code.html").addObject("message", "success");
    }

    @PostMapping("/verify")
    public ModelAndView verifyCode(@RequestParam String code) {
        String currentUsername = verificationCodeService.getCurrentUsername();

        if (currentUsername == null) {
            return new ModelAndView("error").addObject("message", "Session expired. Please try again.");
        }

        boolean isValid = verificationCodeService.validateCode(currentUsername, code);
        if (!isValid) {
            return new ModelAndView("verify-code").addObject("error", "Invalid verification code. Please try again.");
        }

        // Clear the code after successful validation
        verificationCodeService.clearVerificationCode(currentUsername);

        return new ModelAndView("redirect:/change-password.html");
    }

    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam String newPassword, @RequestParam String confirmPassword) {
        String currentUsername = verificationCodeService.getCurrentUsername();

        if (currentUsername == null) {
            return new ModelAndView("error").addObject("message", "Session expired. Please try again.");
        }

        Optional<Account> accountOpt = accountRepository.findByUsername(currentUsername);
        if (accountOpt.isPresent()) {
            if (!newPassword.equals(confirmPassword)) {
                return new ModelAndView("redirect:/change-password.html").addObject("message", "Confirm password does not match.");
            }

            Account account = accountOpt.get();
            // Hash the new password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);
            account.setPassword(hashedPassword);

            // Save the updated account
            accountRepository.save(account);
        }

        return new ModelAndView("redirect:/login.html").addObject("message", "Password changed successfully.");
    }


}




