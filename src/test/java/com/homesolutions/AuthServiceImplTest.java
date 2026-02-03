package com.homesolutions;

import com.homesolutions.dto.AuthResponse;
import com.homesolutions.dto.LoginRequest;
import com.homesolutions.dto.RegisterRequest;
import com.homesolutions.entity.User;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.security.JwtUtil;
import com.homesolutions.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest customerRegisterRequest;
    private RegisterRequest expertRegisterRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        customerRegisterRequest = RegisterRequest.builder()
                .phone("1234567890")
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("password123")
                .role("CUSTOMER")
                .build();

        expertRegisterRequest = RegisterRequest.builder()
                .phone("9876543210")
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("password123")
                .role("EXPERT")
                .build();

        loginRequest = LoginRequest.builder()
                .phone("1234567890")
                .password("password123")
                .build();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        mockUser = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("encodedPassword")
                .roles(roles)
                .enabled(true)
                .build();
    }

    @Test
    void testRegisterCustomer_Success() {
        when(userRepository.existsByPhone(customerRegisterRequest.getPhone())).thenReturn(false);
        when(userRepository.existsByEmail(customerRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(customerRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getPhone())).thenReturn("jwt-token");

        AuthResponse response = authService.register(customerRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getPhone()).isEqualTo("1234567890");
        assertThat(response.getFullName()).isEqualTo("Test Customer");
        assertThat(response.getRoles()).contains("ROLE_CUSTOMER");

        verify(userRepository).existsByPhone(customerRegisterRequest.getPhone());
        verify(userRepository).existsByEmail(customerRegisterRequest.getEmail());
        verify(passwordEncoder).encode(customerRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(mockUser.getPhone());
    }

    @Test
    void testRegisterExpert_Success() {
        Set<String> expertRoles = new HashSet<>();
        expertRoles.add("ROLE_EXPERT");

        User expertUser = User.builder()
                .id(2L)
                .phone("9876543210")
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("encodedPassword")
                .roles(expertRoles)
                .enabled(true)
                .build();

        when(userRepository.existsByPhone(expertRegisterRequest.getPhone())).thenReturn(false);
        when(userRepository.existsByEmail(expertRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(expertRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expertUser);
        when(jwtUtil.generateToken(expertUser.getPhone())).thenReturn("jwt-token-expert");

        AuthResponse response = authService.register(expertRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-expert");
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getRoles()).contains("ROLE_EXPERT");

        verify(userRepository).existsByPhone(expertRegisterRequest.getPhone());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_PhoneAlreadyExists() {
        when(userRepository.existsByPhone(customerRegisterRequest.getPhone())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(customerRegisterRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Phone number already registered");

        verify(userRepository).existsByPhone(customerRegisterRequest.getPhone());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByPhone(loginRequest.getPhone())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getPhone())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getPhone()).isEqualTo("1234567890");

        verify(userRepository).findByPhone(loginRequest.getPhone());
        verify(passwordEncoder).matches(loginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getPhone());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepository.findByPhone(loginRequest.getPhone())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByPhone(loginRequest.getPhone());
        verify(passwordEncoder).matches(loginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
