package com.homesolutions.controller;

import com.homesolutions.dto.*;
import com.homesolutions.service.interfaces.AdminService;
import com.homesolutions.service.interfaces.ServiceManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin-specific endpoints")
public class AdminController {

    private final AdminService adminService;
    private final ServiceManagementService serviceManagementService;

    private String getAuthenticatedPhone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get all users with pagination")
    public ResponseEntity<Page<UserProfileResponse>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        String phone = getAuthenticatedPhone();
        log.info("Get all users requested by admin: {}, page: {}, size: {}", phone, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfileResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{id}/roles")
    @Operation(summary = "Update user roles", description = "Update roles for a specific user")
    public ResponseEntity<UserProfileResponse> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles) {
        String phone = getAuthenticatedPhone();
        log.info("Update roles for user {} by admin: {}", id, phone);
        UserProfileResponse user = adminService.updateUserRoles(id, roles);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Create a new service category")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        String phone = getAuthenticatedPhone();
        log.info("Create category by admin: {}", phone);
        CategoryResponse category = adminService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PostMapping("/services")
    @Operation(summary = "Create service", description = "Create a new service")
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody ServiceRequest request) {
        String phone = getAuthenticatedPhone();
        log.info("Create service by admin: {}", phone);
        ServiceResponse service = serviceManagementService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PostMapping("/experts/{id}/approve")
    @Operation(summary = "Approve expert", description = "Approve an expert application")
    public ResponseEntity<UserProfileResponse> approveExpert(@PathVariable Long id) {
        String phone = getAuthenticatedPhone();
        log.info("Approve expert {} by admin: {}", id, phone);
        UserProfileResponse expert = adminService.approveExpert(id);
        return ResponseEntity.ok(expert);
    }

    @PostMapping("/experts/{id}/reject")
    @Operation(summary = "Reject expert", description = "Reject an expert application")
    public ResponseEntity<UserProfileResponse> rejectExpert(@PathVariable Long id) {
        String phone = getAuthenticatedPhone();
        log.info("Reject expert {} by admin: {}", id, phone);
        UserProfileResponse expert = adminService.rejectExpert(id);
        return ResponseEntity.ok(expert);
    }
}
