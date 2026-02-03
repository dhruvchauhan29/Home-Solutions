package com.homesolutions.service.impl;

import com.homesolutions.dto.AdminLoginRequest;
import com.homesolutions.dto.AdminRegisterRequest;
import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;
import com.homesolutions.entity.User;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.security.JwtUtil;
import com.homesolutions.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        log.debug("Registration request details - email: {}, fullName: {}, role: {}", 
                request.getEmail(), request.getFullName(), request.getRole());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.debug("Registration failed - Email already registered: {}", request.getEmail());
            throw new BusinessException("Email already registered");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            log.debug("Registration failed - Phone already registered: {}", request.getPhone());
            throw new BusinessException("Phone number already registered");
        }

        Set<String> roles = new HashSet<>();
        if ("EXPERT".equalsIgnoreCase(request.getRole())) {
            roles.add("ROLE_EXPERT");
            log.debug("Assigning ROLE_EXPERT to user");
        } else {
            roles.add("ROLE_CUSTOMER");
            log.debug("Assigning ROLE_CUSTOMER to user");
        }

        User user = User.builder()
                .phone(request.getPhone())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());
        log.debug("User details - ID: {}, email: {}, roles: {}", user.getId(), user.getEmail(), user.getRoles());

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        log.debug("Processing login request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.debug("Login failed - User not found with email: {}", request.getEmail());
                    return new ResourceNotFoundException("User not found with email: " + request.getEmail());
                });

        log.debug("User found - ID: {}, enabled: {}", user.getId(), user.getEnabled());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.debug("Login failed - Invalid credentials for email: {}", request.getEmail());
            throw new BusinessException("Invalid credentials");
        }

        if (!user.getEnabled()) {
            log.debug("Login failed - Account disabled for email: {}", request.getEmail());
            throw new BusinessException("Account is disabled");
        }

        log.info("User logged in successfully: {}", user.getEmail());
        log.debug("Generating JWT token for user ID: {}", user.getId());

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        log.info("Registering new admin with email: {}", request.getEmail());
        log.debug("Admin registration request details - email: {}, fullName: {}", 
                request.getEmail(), request.getFullName());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.debug("Admin registration failed - Email already registered: {}", request.getEmail());
            throw new BusinessException("Email already registered");
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        log.debug("Assigning ROLE_ADMIN to user");

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("Admin registered successfully with ID: {}", user.getId());
        log.debug("Admin details - ID: {}, email: {}, roles: {}", user.getId(), user.getEmail(), user.getRoles());

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginAdmin(AdminLoginRequest request) {
        log.info("Admin login attempt for email: {}", request.getEmail());
        log.debug("Processing admin login request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.debug("Admin login failed - User not found with email: {}", request.getEmail());
                    return new ResourceNotFoundException("User not found with email: " + request.getEmail());
                });

        log.debug("User found - ID: {}, enabled: {}, roles: {}", user.getId(), user.getEnabled(), user.getRoles());

        if (!user.getRoles().contains("ROLE_ADMIN")) {
            log.debug("Admin login failed - User does not have ROLE_ADMIN: {}", request.getEmail());
            throw new BusinessException("User is not an admin");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.debug("Admin login failed - Invalid credentials for email: {}", request.getEmail());
            throw new BusinessException("Invalid credentials");
        }

        if (!user.getEnabled()) {
            log.debug("Admin login failed - Account disabled for email: {}", request.getEmail());
            throw new BusinessException("Account is disabled");
        }

        log.info("Admin logged in successfully: {}", user.getEmail());
        log.debug("Generating JWT token for admin ID: {}", user.getId());

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }
}
