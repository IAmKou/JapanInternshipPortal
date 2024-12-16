package com.example.jip.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
@TestConfiguration
public class Config {
     @Bean
     public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
         return http.cors(cors -> cors.configurationSource(corsConfigratiobSource()))
                 .csrf(AbstractHttpConfigurer::disable)
                 .authorizeRequests(
                         auth -> auth.anyRequest().permitAll()

                 )
                 .build();

     }

     CorsConfigurationSource corsConfigratiobSource(){
         CorsConfiguration configuration = new CorsConfiguration();
         configuration.addAllowedHeader("*");
         configuration.addAllowedOrigin("*");
         configuration.addAllowedMethod("*");
         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", configuration);
         return source;
     }
}