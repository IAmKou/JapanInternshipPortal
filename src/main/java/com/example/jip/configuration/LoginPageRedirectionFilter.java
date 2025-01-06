package com.example.jip.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginPageRedirectionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/login.html")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getAuthorities().isEmpty()) {
                String redirectUrl = "/student.html"; // Default to student
                if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
                    redirectUrl = "/admin.html";
                } else if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("TEACHER"))) {
                    redirectUrl = "/teacher.html";
                } else if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("MANAGER"))) {
                    redirectUrl = "/manager.html";
                }
                response.sendRedirect(redirectUrl);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
