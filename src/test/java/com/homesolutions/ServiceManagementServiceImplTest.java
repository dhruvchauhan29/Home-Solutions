package com.homesolutions;

import com.homesolutions.dto.ServiceRequest;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.Service;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.CategoryRepository;
import com.homesolutions.repository.ServiceRepository;
import com.homesolutions.service.impl.ServiceManagementServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceManagementServiceImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ServiceManagementServiceImpl serviceManagementService;

    private Category mockCategory;
    private Service mockService;
    private ServiceRequest serviceRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        mockService = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .category(mockCategory)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        serviceRequest = ServiceRequest.builder()
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .categoryId(1L)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllServices() {
        Page<Service> servicePage = new PageImpl<>(Collections.singletonList(mockService));
        when(serviceRepository.findAll(pageable)).thenReturn(servicePage);

        Page<ServiceResponse> response = serviceManagementService.getAllServices(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Pipe Repair");
        assertThat(response.getContent().get(0).getCategoryName()).isEqualTo("Plumbing");

        verify(serviceRepository).findAll(pageable);
    }

    @Test
    void testSearchServices() {
        Page<Service> servicePage = new PageImpl<>(Collections.singletonList(mockService));
        when(serviceRepository.searchServices(1L, "pipe", pageable)).thenReturn(servicePage);

        Page<ServiceResponse> response = serviceManagementService.searchServices(1L, "pipe", pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Pipe Repair");

        verify(serviceRepository).searchServices(1L, "pipe", pageable);
    }

    @Test
    void testGetServiceById() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        ServiceResponse response = serviceManagementService.getServiceById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pipe Repair");
        assertThat(response.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Plumbing");
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(response.getActive()).isTrue();

        verify(serviceRepository).findById(1L);
    }

    @Test
    void testGetServiceById_NotFound() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceManagementService.getServiceById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service not found with ID: 1");

        verify(serviceRepository).findById(1L);
    }

    @Test
    void testCreateService() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(serviceRepository.save(any(Service.class))).thenReturn(mockService);

        ServiceResponse response = serviceManagementService.createService(serviceRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Pipe Repair");
        assertThat(response.getCategoryName()).isEqualTo("Plumbing");
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));

        verify(categoryRepository).findById(1L);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void testCreateService_CategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceManagementService.createService(serviceRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with ID: 1");

        verify(categoryRepository).findById(1L);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void testCreateService_WithDefaultExtraHourlyRate() {
        ServiceRequest requestWithoutRate = ServiceRequest.builder()
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .categoryId(1L)
                .basePrice(BigDecimal.valueOf(150.00))
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(serviceRepository.save(any(Service.class))).thenReturn(mockService);

        ServiceResponse response = serviceManagementService.createService(requestWithoutRate);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Pipe Repair");

        verify(categoryRepository).findById(1L);
        verify(serviceRepository).save(any(Service.class));
    }
}
