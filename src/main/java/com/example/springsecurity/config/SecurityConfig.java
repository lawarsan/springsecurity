package com.example.springsecurity.config;

import com.example.springsecurity.filters.EntitlementFilter;
import com.example.springsecurity.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EntitlementFilter entitlementFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, EntitlementFilter entitlementFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.entitlementFilter = entitlementFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Allow public authentication endpoints
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                // Add JWT Authentication Filter first
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Add Entitlement Filter after JWT Authentication
                .addFilterAfter(entitlementFilter, JwtAuthenticationFilter.class)
                .build();
    }
}
