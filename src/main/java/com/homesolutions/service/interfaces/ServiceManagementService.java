package com.homesolutions.service.interfaces;

import com.homesolutions.dto.ServiceRequest;
import com.homesolutions.dto.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceManagementService {
    Page<ServiceResponse> getAllServices(Pageable pageable);
    Page<ServiceResponse> searchServices(Long categoryId, String search, Pageable pageable);
    ServiceResponse getServiceById(Long id);
    ServiceResponse createService(ServiceRequest request);
}
