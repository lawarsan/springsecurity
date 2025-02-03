package com.example.springsecurity.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "YourVerySecretKeyThatShouldBeAtLeast32BytesLong";
    private final Map<String, UserDetails> users = new HashMap<>();


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public UserDetails loadUserByUsername(String username) {
        users.put("bob", User.withUsername("bob")
                .password("{noop}password") // No encoding for simplicity
                .roles("USER")
                .build());

        users.put("alice", User.withUsername("alice")
                .password("{noop}password")
                .roles("ADMIN")
                .build());

        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return users.get(username);
    }
}

