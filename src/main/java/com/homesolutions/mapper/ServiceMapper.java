package com.homesolutions.mapper;

import com.homesolutions.dto.ServiceRequest;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.Service;

import java.math.BigDecimal;

public class ServiceMapper {

    private ServiceMapper() {
    }

    public static ServiceResponse toResponse(Service service) {
        if (service == null) {
            return null;
        }

        Category category = service.getCategory();
        
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .basePrice(service.getBasePrice())
                .extraHourlyRate(service.getExtraHourlyRate())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .build();
    }

    public static Service toEntity(ServiceRequest request, Category category) {
        if (request == null) {
            return null;
        }

        return Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .basePrice(request.getBasePrice())
                .extraHourlyRate(request.getExtraHourlyRate() != null ? 
                        request.getExtraHourlyRate() : BigDecimal.valueOf(90.00))
                .build();
    }

    public static void updateEntityFromRequest(Service service, ServiceRequest request, Category category) {
        if (service == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (category != null) {
            service.setCategory(category);
        }
        if (request.getBasePrice() != null) {
            service.setBasePrice(request.getBasePrice());
        }
        if (request.getExtraHourlyRate() != null) {
            service.setExtraHourlyRate(request.getExtraHourlyRate());
        }
    }
}
