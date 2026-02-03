package com.homesolutions.service.impl;

import com.homesolutions.dto.CategoryRequest;
import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.User;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.CategoryRepository;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable).map(this::mapToUserProfileResponse);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserRoles(Long userId, Set<String> roles) {
        log.info("Updating roles for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<String> normalizedRoles = new HashSet<>();
        for (String role : roles) {
            if (!role.startsWith("ROLE_")) {
                normalizedRoles.add("ROLE_" + role);
            } else {
                normalizedRoles.add(role);
            }
        }

        user.setRoles(normalizedRoles);
        user = userRepository.save(user);

        log.info("Roles updated successfully for user: {}", user.getId());
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessException("Category already exists with name: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();

        category = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", category.getId());

        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public UserProfileResponse approveExpert(Long expertId) {
        log.info("Approving expert with ID: {}", expertId);

        User expert = userRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + expertId));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        expert.setEnabled(true);
        expert = userRepository.save(expert);

        log.info("Expert approved successfully: {}", expert.getId());
        return mapToUserProfileResponse(expert);
    }

    @Override
    @Transactional
    public UserProfileResponse rejectExpert(Long expertId) {
        log.info("Rejecting expert with ID: {}", expertId);

        User expert = userRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + expertId));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        expert.setEnabled(false);
        expert = userRepository.save(expert);

        log.info("Expert rejected successfully: {}", expert.getId());
        return mapToUserProfileResponse(expert);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }
}
