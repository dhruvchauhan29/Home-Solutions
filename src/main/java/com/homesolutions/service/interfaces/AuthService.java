package com.homesolutions.service.interfaces;

import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
