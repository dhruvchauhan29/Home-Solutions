package com.homesolutions;

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
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("password123")
                .role("CUSTOMER")
                .build();

        expertRegisterRequest = RegisterRequest.builder()
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("password123")
                .role("EXPERT")
                .build();

        loginRequest = LoginRequest.builder()
                .email("customer@test.com")
                .password("password123")
                .build();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        mockUser = User.builder()
                .id(1L)
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("encodedPassword")
                .roles(roles)
                .enabled(true)
                .build();
    }

    @Test
    void testRegisterCustomer_Success() {
        when(userRepository.existsByEmail(customerRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(customerRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.register(customerRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("customer@test.com");
        assertThat(response.getFullName()).isEqualTo("Test Customer");
        assertThat(response.getRoles()).contains("ROLE_CUSTOMER");

        verify(userRepository).existsByEmail(customerRegisterRequest.getEmail());
        verify(passwordEncoder).encode(customerRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(mockUser.getEmail());
    }

    @Test
    void testRegisterExpert_Success() {
        Set<String> expertRoles = new HashSet<>();
        expertRoles.add("ROLE_EXPERT");

        User expertUser = User.builder()
                .id(2L)
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("encodedPassword")
                .roles(expertRoles)
                .enabled(true)
                .build();

        when(userRepository.existsByEmail(expertRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(expertRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expertUser);
        when(jwtUtil.generateToken(expertUser.getEmail())).thenReturn("jwt-token-expert");

        AuthResponse response = authService.register(expertRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-expert");
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getRoles()).contains("ROLE_EXPERT");

        verify(userRepository).existsByEmail(expertRegisterRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_PhoneAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .phone("1234567890")
                .email("newuser@test.com")
                .fullName("New User")
                .password("password123")
                .role("CUSTOMER")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Phone number already registered");

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByPhone(request.getPhone());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByEmail(customerRegisterRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(customerRegisterRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email already registered");

        verify(userRepository).existsByEmail(customerRegisterRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("customer@test.com");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getEmail());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRegisterAdmin_Success() {
        AdminRegisterRequest adminRegisterRequest = AdminRegisterRequest.builder()
                .email("admin@test.com")
                .fullName("Test Admin")
                .password("password123")
                .build();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("ROLE_ADMIN");

        User adminUser = User.builder()
                .id(3L)
                .email("admin@test.com")
                .fullName("Test Admin")
                .password("encodedPassword")
                .roles(adminRoles)
                .enabled(true)
                .build();

        when(userRepository.existsByEmail(adminRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(adminRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtUtil.generateToken(adminUser.getEmail())).thenReturn("jwt-token-admin");

        AuthResponse response = authService.registerAdmin(adminRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-admin");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(3L);
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getRoles()).contains("ROLE_ADMIN");

        verify(userRepository).existsByEmail(adminRegisterRequest.getEmail());
        verify(passwordEncoder).encode(adminRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(adminUser.getEmail());
    }

    @Test
    void testRegisterAdmin_EmailAlreadyExists() {
        AdminRegisterRequest adminRegisterRequest = AdminRegisterRequest.builder()
                .email("admin@test.com")
                .fullName("Test Admin")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(adminRegisterRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.registerAdmin(adminRegisterRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email already registered");

        verify(userRepository).existsByEmail(adminRegisterRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginAdmin_Success() {
        AdminLoginRequest adminLoginRequest = AdminLoginRequest.builder()
                .email("admin@test.com")
                .password("password123")
                .build();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("ROLE_ADMIN");

        User adminUser = User.builder()
                .id(3L)
                .email("admin@test.com")
                .fullName("Test Admin")
                .password("encodedPassword")
                .roles(adminRoles)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(adminLoginRequest.getEmail())).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches(adminLoginRequest.getPassword(), adminUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(adminUser.getEmail())).thenReturn("jwt-token-admin");

        AuthResponse response = authService.loginAdmin(adminLoginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-admin");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(3L);
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getRoles()).contains("ROLE_ADMIN");

        verify(userRepository).findByEmail(adminLoginRequest.getEmail());
        verify(passwordEncoder).matches(adminLoginRequest.getPassword(), adminUser.getPassword());
        verify(jwtUtil).generateToken(adminUser.getEmail());
    }

    @Test
    void testLoginAdmin_UserNotAdmin() {
        AdminLoginRequest adminLoginRequest = AdminLoginRequest.builder()
                .email("customer@test.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(adminLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> authService.loginAdmin(adminLoginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User is not an admin");

        verify(userRepository).findByEmail(adminLoginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testLoginAdmin_InvalidCredentials() {
        AdminLoginRequest adminLoginRequest = AdminLoginRequest.builder()
                .email("admin@test.com")
                .password("wrongpassword")
                .build();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("ROLE_ADMIN");

        User adminUser = User.builder()
                .id(3L)
                .email("admin@test.com")
                .fullName("Test Admin")
                .password("encodedPassword")
                .roles(adminRoles)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(adminLoginRequest.getEmail())).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches(adminLoginRequest.getPassword(), adminUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.loginAdmin(adminLoginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByEmail(adminLoginRequest.getEmail());
        verify(passwordEncoder).matches(adminLoginRequest.getPassword(), adminUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
