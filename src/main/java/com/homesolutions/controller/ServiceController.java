package com.homesolutions.controller;

import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.service.interfaces.ServiceManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service management endpoints")
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    @Operation(summary = "Get all services", description = "Get all services with pagination and optional filters")
    public ResponseEntity<Page<ServiceResponse>> getAllServices(
            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Search by name or description")
            @RequestParam(required = false) String search,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sort) {
        
        log.info("Get all services - categoryId: {}, search: {}, page: {}, size: {}, sort: {}", 
                categoryId, search, page, size, sort);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        
        Page<ServiceResponse> services;
        if (categoryId != null || search != null) {
            services = serviceManagementService.searchServices(categoryId, search, pageable);
        } else {
            services = serviceManagementService.getAllServices(pageable);
        }
        
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID", description = "Get a specific service by its ID")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        log.info("Get service by ID: {}", id);
        ServiceResponse service = serviceManagementService.getServiceById(id);
        return ResponseEntity.ok(service);
    }
}
