package com.example.jip.controller;

import com.example.jip.dto.AccountDTO;
import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import com.example.jip.services.AccountServices;
import com.example.jip.services.EmailServices;
import com.example.jip.services.VerificationCodeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Get all users
    @GetMapping
    public List<AccountDTO> getAllUsers() {
        return accountServices.getAllAccountDTOs();
    }

    // Get total account count
    @GetMapping("/count")
    public long getTotalAccounts() {
        return accountRepository.count();
    }

    // Update user information
    @PostMapping("/{id}")
    public AccountDTO updateUser(@PathVariable int id, @ModelAttribute AccountDTO accountDTO) {
        return accountServices.updateAccount(id, accountDTO);
    }

    // Check if user exists and send verification code if email is found
    @GetMapping("/check")
    public ModelAndView checkUser(@RequestParam(name = "username", required = true) String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        if (accountOpt.isEmpty()) {
            return new ModelAndView("redirect:/forgot-password.html?message=Account not found.");
        }

        Account account = accountOpt.get();
        AccountDTO accountDTO = new AccountDTO(account);
        String email = accountDTO.getEmail();

        if (email == null || email.trim().isEmpty()) {
            return new ModelAndView("redirect:/forgot-password.html?message=Email address is empty for this account.");
        }

        String verifyCode = emailServices.sendVerificationCode(email);
        if ("ERROR_EMPTY_EMAIL".equals(verifyCode)) {
            return new ModelAndView("redirect:/forgot-password.html?message=Email address is empty.");
        } else if ("ERROR_SENDING_EMAIL".equals(verifyCode)) {
            return new ModelAndView("redirect:/forgot-password.html?message=Failed to send verification email. Please try again.");
        }

        // Save the verification code and username
        verificationCodeService.setCurrentUsername(username);
        verificationCodeService.saveVerificationCode(username, verifyCode);

        return new ModelAndView("redirect:/verify-code.html?message=Verification code sent successfully.");
    }

    // Verify the code sent to the user
    @PostMapping("/verify")
    public ModelAndView verifyCode(@RequestParam String code) {
        String currentUsername = verificationCodeService.getCurrentUsername();

        if (currentUsername == null) {
            return new ModelAndView("redirect:/verify-code.html?message=Session expired. Please try again.");
        }

        boolean isValid = verificationCodeService.validateCode(currentUsername, code);
        if (!isValid) {
            return new ModelAndView("redirect:/verify-code.html?message=Invalid verification code. Please try again.");
        }

        verificationCodeService.clearVerificationCode(currentUsername);

        return new ModelAndView("redirect:/reset-password.html");
    }

    // Change password for a user (requires current password validation)
    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       @RequestParam int uid,
                                       RedirectAttributes redirectAttributes) {
        Optional<Account> accountOpt = accountRepository.findById(uid);

        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Account not found.");
            return new ModelAndView("redirect:/change-password.html");
        }

        Account account = accountOpt.get();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Validate confirm password
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("message", "Re-enter Password does not match new password.");
            return new ModelAndView("redirect:/change-password.html");
        }

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "Old Password does not match current password.");
            return new ModelAndView("redirect:/change-password.html");
        }

        // Update password
        String hashedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(hashedPassword);
        accountRepository.save(account);

        // Determine redirect based on role
        String roleName = account.getRole().getName();
        String redirectUrl = switch (roleName) {
            case "STUDENT" -> "redirect:/users-profile-student.html";
            case "TEACHER" -> "redirect:/users-profile-teacher.html";
            default -> "redirect:/users-profile-manager.html";
        };

        redirectAttributes.addFlashAttribute("message", "Password changed successfully.");
        return new ModelAndView(redirectUrl);
    }

    // Check current password for validity
    @PostMapping("/check-current-password")
    @ResponseBody
    public Map<String, Object> checkCurrentPassword(@RequestBody Map<String, String> requestData) {
        String currentPassword = requestData.get("currentPassword");
        String currentUsername = verificationCodeService.getCurrentUsername();

        Map<String, Object> response = new HashMap<>();
        if (currentUsername == null) {
            response.put("isMatch", false);
            return response;  // Session expired
        }

        Optional<Account> accountOpt = accountRepository.findByUsername(currentUsername);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Compare the provided password with the stored hashed password
            boolean isPasswordMatch = passwordEncoder.matches(currentPassword, account.getPassword());

            response.put("isMatch", isPasswordMatch);
        } else {
            response.put("isMatch", false);
        }

        return response;
    }

    // Reset password after verification
    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@RequestParam String newPassword, @RequestParam String confirmPassword) {
        String currentUsername = verificationCodeService.getCurrentUsername();

        if (currentUsername == null) {
            return new ModelAndView("redirect:/reset-password.html?message=Session expired. Please try again.");
        }

        Optional<Account> accountOpt = accountRepository.findByUsername(currentUsername);
        if (accountOpt.isPresent()) {
            if (!newPassword.equals(confirmPassword)) {
                return new ModelAndView("redirect:/reset-password.html?message=Confirm password does not match new password.");
            }

            Account account = accountOpt.get();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);
            account.setPassword(hashedPassword);

            accountRepository.save(account);
        }

        return new ModelAndView("redirect:/login.html?message=Password reset successfully.");
    }
}