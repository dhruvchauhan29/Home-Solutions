package com.homesolutions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homesolutions.controller.AuthController;
import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;
import com.homesolutions.security.CustomUserDetailsService;
import com.homesolutions.security.JwtUtil;
import com.homesolutions.service.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .phone("1234567890")
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role("CUSTOMER")
                .build();

        loginRequest = LoginRequest.builder()
                .phone("1234567890")
                .password("password123")
                .build();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        authResponse = AuthResponse.builder()
                .token("jwt-token-123")
                .type("Bearer")
                .userId(1L)
                .phone("1234567890")
                .fullName("Test User")
                .roles(roles)
                .build();
    }

    @Test
    void testRegister_Success() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    void testLogin_Success() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    void testRegister_ValidationError() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .phone("123")
                .email("test@example.com")
                .fullName("T")
                .password("123")
                .role("CUSTOMER")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
