package com.homesolutions;

import com.homesolutions.dto.QuoteRequest;
import com.homesolutions.dto.QuoteResponse;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.Service;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.ServiceRepository;
import com.homesolutions.service.impl.PricingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private Service mockService;
    private Category mockCategory;

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
                .description("Professional pipe repair service")
                .category(mockCategory)
                .basePrice(BigDecimal.valueOf(500.00))
                .extraHourlyRate(BigDecimal.valueOf(100.00))
                .active(true)
                .build();
    }

    @Test
    void testCalculateQuote_BasePrice() {
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(1L)
                .durationMinutes(60)
                .build();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response).isNotNull();
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getExtraCharge()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getDetails()).contains("Base price: ₹500.00");

        verify(serviceRepository).findById(1L);
    }

    @Test
    void testCalculateQuote_WithExtraHours() {
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(1L)
                .durationMinutes(150)
                .build();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response).isNotNull();
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getExtraCharge()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(700.00));
        assertThat(response.getDetails()).contains("Extra charge: ₹200.00");

        verify(serviceRepository).findById(1L);
    }

    @Test
    void testCalculateQuote_WithCoupon() {
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(1L)
                .durationMinutes(60)
                .build();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        QuoteResponse response = pricingService.calculateQuote(request);

        assertThat(response).isNotNull();
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));

        verify(serviceRepository).findById(1L);
    }

    @Test
    void testCalculateQuote_ServiceNotFound() {
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(999L)
                .durationMinutes(60)
                .build();

        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateQuote(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service not found with ID: 999");

        verify(serviceRepository).findById(999L);
    }
}
