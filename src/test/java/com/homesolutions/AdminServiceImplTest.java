package com.homesolutions;

import com.homesolutions.dto.CategoryRequest;
import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.User;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.CategoryRepository;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User mockUser;
    private User mockExpert;
    private Category mockCategory;
    private CategoryRequest categoryRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        Set<String> customerRoles = new HashSet<>();
        customerRoles.add("ROLE_CUSTOMER");

        mockUser = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("encodedPassword")
                .roles(customerRoles)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        Set<String> expertRoles = new HashSet<>();
        expertRoles.add("ROLE_EXPERT");

        mockExpert = User.builder()
                .id(2L)
                .phone("9876543210")
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("encodedPassword")
                .roles(expertRoles)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        mockCategory = Category.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        categoryRequest = CategoryRequest.builder()
                .name("Plumbing")
                .description("Plumbing services")
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllUsers() {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(mockUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserProfileResponse> response = adminService.getAllUsers(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getPhone()).isEqualTo("1234567890");
        assertThat(response.getContent().get(0).getFullName()).isEqualTo("Test Customer");

        verify(userRepository).findAll(pageable);
    }

    @Test
    void testUpdateUserRoles() {
        Set<String> newRoles = new HashSet<>();
        newRoles.add("ADMIN");
        newRoles.add("CUSTOMER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserProfileResponse response = adminService.updateUserRoles(1L, newRoles);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserRoles_UserNotFound() {
        Set<String> newRoles = new HashSet<>();
        newRoles.add("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateUserRoles(1L, newRoles))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserRoles_WithRolePrefix() {
        Set<String> newRoles = new HashSet<>();
        newRoles.add("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserProfileResponse response = adminService.updateUserRoles(1L, newRoles);

        assertThat(response).isNotNull();

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.findByName("Plumbing")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);

        CategoryResponse response = adminService.createCategory(categoryRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Plumbing");
        assertThat(response.getDescription()).isEqualTo("Plumbing services");
        assertThat(response.getActive()).isTrue();

        verify(categoryRepository).findByName("Plumbing");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategory_AlreadyExists() {
        when(categoryRepository.findByName("Plumbing")).thenReturn(Optional.of(mockCategory));

        assertThatThrownBy(() -> adminService.createCategory(categoryRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category already exists with name: Plumbing");

        verify(categoryRepository).findByName("Plumbing");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testApproveExpert() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockExpert));
        when(userRepository.save(any(User.class))).thenReturn(mockExpert);

        UserProfileResponse response = adminService.approveExpert(2L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);

        verify(userRepository).findById(2L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testApproveExpert_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.approveExpert(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with ID: 2");

        verify(userRepository).findById(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testApproveExpert_NotAnExpert() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> adminService.approveExpert(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User is not an expert");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRejectExpert() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockExpert));
        when(userRepository.save(any(User.class))).thenReturn(mockExpert);

        UserProfileResponse response = adminService.rejectExpert(2L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);

        verify(userRepository).findById(2L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRejectExpert_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.rejectExpert(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with ID: 2");

        verify(userRepository).findById(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRejectExpert_NotAnExpert() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> adminService.rejectExpert(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User is not an expert");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
