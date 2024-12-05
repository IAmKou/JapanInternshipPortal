package com.example.jip.configuration;

import com.example.jip.entity.Account;
import com.example.jip.entity.Role;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.RoleRepository;
import com.example.jip.services.CustomUserDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomUserDetailServices customUserDetailsService;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler, CustomUserDetailServices customUserDetailsService) {
        this.successHandler = successHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin.html").hasAuthority("ADMIN")
                        .requestMatchers("/student.html", "list-student-assignment.html").hasAuthority("STUDENT")
                        .requestMatchers("/teacher.html", "add-assignment.html", "list-assignment.html").hasAuthority("TEACHER")
                        .requestMatchers("/manager.html").hasAuthority("MANAGER")
                        .requestMatchers("/css/**", "/js/**", "/images/**","/img/**",
                                "/webfonts/**","/fonts/**","/hts-cache/**","/style.css","/forgot-password.html",
                                "/verify-code.html","/change-password.html","/users/check","/users/verify","/users/change-password")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login.html")
                        .loginProcessingUrl("/perform_login")
                        .failureUrl("/login.html?error=true")// Redirect on failure
                        .successHandler(successHandler)//Redirect on success
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout=true")
                        .permitAll());
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    AppConfig appConfig = new AppConfig();

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(appConfig.passwordEncoder());
    }

    @Bean
    public CommandLineRunner createDefaultAccount() {
        return args -> {
            String defaultUsername = "admin";
            String defaultPassword = "123456";
            String defaultRole = "ADMIN";

            Role adminRole = roleRepository.findByName(defaultRole)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(defaultRole);
                        return roleRepository.save(role);
                    });

            if (!accountRepository.existsByUsername(defaultUsername)) {
                Account adminAccount = new Account();
                adminAccount.setUsername(defaultUsername);
                adminAccount.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
                adminAccount.setRole(adminRole);
                accountRepository.save(adminAccount);
                System.out.println("Default admin account created successfully.");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
