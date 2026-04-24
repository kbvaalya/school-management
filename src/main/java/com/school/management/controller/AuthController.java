package com.school.management.controller;

import com.school.management.dto.ApiResponse;
import com.school.management.dto.auth.AuthResponse;
import com.school.management.dto.auth.LoginRequest;
import com.school.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(request)));
    }
}
