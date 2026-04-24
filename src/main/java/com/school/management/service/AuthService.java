package com.school.management.service;

import com.school.management.dto.auth.AuthResponse;
import com.school.management.dto.auth.LoginRequest;
import com.school.management.entity.User;
import com.school.management.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager; this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);
        log.info("User '{}' logged in with role {}", user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    }
}
