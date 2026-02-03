package com.homesolutions.controller;

import com.homesolutions.dto.AdminLoginRequest;
import com.homesolutions.dto.AdminRegisterRequest;
import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;
import com.homesolutions.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user (CUSTOMER or EXPERT)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for email: {}", request.getEmail());
        log.debug("Processing registration - email: {}, role: {}", request.getEmail(), request.getRole());
        AuthResponse response = authService.register(request);
        log.debug("Registration successful for email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        log.debug("Processing login request for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.debug("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout user (client should discard token)")
    public ResponseEntity<Map<String, String>> logout() {
        log.info("Logout request");
        log.debug("Processing logout request");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/admin/register")
    @Operation(summary = "Register admin", description = "Register a new admin user")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        log.info("Admin register request for email: {}", request.getEmail());
        log.debug("Processing admin registration - email: {}", request.getEmail());
        AuthResponse response = authService.registerAdmin(request);
        log.debug("Admin registration successful for email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Login admin", description = "Authenticate admin and return JWT token")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody AdminLoginRequest request) {
        log.info("Admin login request for email: {}", request.getEmail());
        log.debug("Processing admin login request for email: {}", request.getEmail());
        AuthResponse response = authService.loginAdmin(request);
        log.debug("Admin login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
}
