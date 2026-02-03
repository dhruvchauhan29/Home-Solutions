package com.homesolutions.service.impl;

import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.dto.ServiceRequest;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.Service;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.CategoryRepository;
import com.homesolutions.repository.ServiceRepository;
import com.homesolutions.service.interfaces.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAllServices(Pageable pageable) {
        log.info("Fetching all services with pagination");
        return serviceRepository.findAll(pageable).map(this::mapToServiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> searchServices(Long categoryId, String search, Pageable pageable) {
        log.info("Searching services with categoryId: {}, search: {}", categoryId, search);
        return serviceRepository.searchServices(categoryId, search, pageable)
                .map(this::mapToServiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        log.info("Fetching service by ID: {}", id);
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        return mapToServiceResponse(service);
    }

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        log.info("Creating new service: {}", request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Service service = Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .basePrice(request.getBasePrice())
                .extraHourlyRate(request.getExtraHourlyRate() != null ? request.getExtraHourlyRate() : java.math.BigDecimal.valueOf(90.00))
                .active(true)
                .build();

        service = serviceRepository.save(service);
        log.info("Service created successfully with ID: {}", service.getId());

        return mapToServiceResponse(service);
    }

    private ServiceResponse mapToServiceResponse(Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(service.getCategory().getId())
                .categoryName(service.getCategory().getName())
                .basePrice(service.getBasePrice())
                .extraHourlyRate(service.getExtraHourlyRate())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
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
