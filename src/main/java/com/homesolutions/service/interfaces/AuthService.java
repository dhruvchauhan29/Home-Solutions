package com.homesolutions.service.interfaces;

import com.homesolutions.dto.AdminLoginRequest;
import com.homesolutions.dto.AdminRegisterRequest;
import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse registerAdmin(AdminRegisterRequest request);
    AuthResponse loginAdmin(AdminLoginRequest request);
}
