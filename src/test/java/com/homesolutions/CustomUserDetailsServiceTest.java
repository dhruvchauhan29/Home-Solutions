package com.homesolutions;

import com.homesolutions.entity.Admin;
import com.homesolutions.entity.User;
import com.homesolutions.repository.AdminRepository;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Admin testAdmin;
    private User testCustomer;
    private User testExpert;

    @BeforeEach
    void setUp() {
        testAdmin = Admin.builder()
                .id(1L)
                .email("admin@test.com")
                .password("encodedPassword")
                .fullName("Test Admin")
                .enabled(true)
                .build();

        testCustomer = User.builder()
                .id(1L)
                .email("customer@test.com")
                .password("encodedPassword")
                .fullName("Test Customer")
                .enabled(true)
                .roles(Set.of("ROLE_CUSTOMER"))
                .build();

        testExpert = User.builder()
                .id(2L)
                .email("expert@test.com")
                .password("encodedPassword")
                .fullName("Test Expert")
                .enabled(true)
                .roles(Set.of("ROLE_EXPERT"))
                .build();
    }

    @Test
    void loadUserByUsername_AdminUser_ReturnsAdminUserDetails() {
        // Given
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_CustomerUser_ReturnsCustomerUserDetails() {
        // Given
        when(adminRepository.findByEmail("customer@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("customer@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("customer@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_CUSTOMER");
    }

    @Test
    void loadUserByUsername_ExpertUser_ReturnsExpertUserDetails() {
        // Given
        when(adminRepository.findByEmail("expert@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(testExpert));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("expert@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("expert@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_EXPERT");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        // Given
        when(adminRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: nonexistent@test.com");
    }

    @Test
    void loadUserByUsername_DisabledAdmin_ReturnsDisabledUserDetails() {
        // Given
        Admin disabledAdmin = Admin.builder()
                .id(2L)
                .email("disabled@test.com")
                .password("encodedPassword")
                .fullName("Disabled Admin")
                .enabled(false)
                .build();
        when(adminRepository.findByEmail("disabled@test.com")).thenReturn(Optional.of(disabledAdmin));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("disabled@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_DisabledCustomer_ReturnsDisabledUserDetails() {
        // Given
        User disabledCustomer = User.builder()
                .id(3L)
                .email("disabled-customer@test.com")
                .password("encodedPassword")
                .fullName("Disabled Customer")
                .enabled(false)
                .roles(Set.of("ROLE_CUSTOMER"))
                .build();
        when(adminRepository.findByEmail("disabled-customer@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("disabled-customer@test.com")).thenReturn(Optional.of(disabledCustomer));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("disabled-customer@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_UserWithMultipleRoles_ReturnsAllRoles() {
        // Given
        User userWithMultipleRoles = User.builder()
                .id(4L)
                .email("multi-role@test.com")
                .password("encodedPassword")
                .fullName("Multi Role User")
                .enabled(true)
                .roles(Set.of("ROLE_CUSTOMER", "ROLE_EXPERT"))
                .build();
        when(adminRepository.findByEmail("multi-role@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("multi-role@test.com")).thenReturn(Optional.of(userWithMultipleRoles));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("multi-role@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()))
                .containsExactlyInAnyOrder("ROLE_CUSTOMER", "ROLE_EXPERT");
    }

    @Test
    void loadUserByUsername_AdminTakesPrecedenceOverUser() {
        // Given - both admin and user exist with same email
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));
        // User repository should not be called when admin is found

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@test.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}
