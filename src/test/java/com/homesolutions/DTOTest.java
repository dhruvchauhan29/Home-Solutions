package com.homesolutions;

import com.homesolutions.dto.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DTOTest {

    @Test
    void testAuthResponse() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .type("Bearer")
                .userId(1L)
                .email("test@example.com")
                .fullName("Test User")
                .roles(roles)
                .build();

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getRoles()).contains("ROLE_CUSTOMER");
    }

    @Test
    void testLoginRequest() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void testRegisterRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .phone("1234567890")
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role("CUSTOMER")
                .build();

        assertThat(request.getPhone()).isEqualTo("1234567890");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getFullName()).isEqualTo("Test User");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void testAdminRegisterRequest() {
        AdminRegisterRequest request = AdminRegisterRequest.builder()
                .email("admin@example.com")
                .fullName("Test Admin")
                .password("password123")
                .build();

        assertThat(request.getEmail()).isEqualTo("admin@example.com");
        assertThat(request.getFullName()).isEqualTo("Test Admin");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void testAdminLoginRequest() {
        AdminLoginRequest request = AdminLoginRequest.builder()
                .email("admin@example.com")
                .password("password123")
                .build();

        assertThat(request.getEmail()).isEqualTo("admin@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void testUserProfileResponse() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        UserProfileResponse response = UserProfileResponse.builder()
                .id(1L)
                .phone("1234567890")
                .email("test@example.com")
                .fullName("Test User")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPhone()).isEqualTo("1234567890");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getRoles()).contains("ROLE_CUSTOMER");
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void testServiceRequest() {
        ServiceRequest request = ServiceRequest.builder()
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .categoryId(1L)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .build();

        assertThat(request.getName()).isEqualTo("Pipe Repair");
        assertThat(request.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(request.getCategoryId()).isEqualTo(1L);
        assertThat(request.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(request.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void testServiceResponse() {
        ServiceResponse response = ServiceResponse.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .categoryId(1L)
                .categoryName("Plumbing")
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pipe Repair");
        assertThat(response.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Plumbing");
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(response.getActive()).isTrue();
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void testCategoryRequest() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Plumbing")
                .description("Plumbing services")
                .build();

        assertThat(request.getName()).isEqualTo("Plumbing");
        assertThat(request.getDescription()).isEqualTo("Plumbing services");
    }

    @Test
    void testCategoryResponse() {
        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Plumbing");
        assertThat(response.getDescription()).isEqualTo("Plumbing services");
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void testAddressRequest() {
        AddressRequest request = AddressRequest.builder()
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .landmark("Near Park")
                .isDefault(true)
                .build();

        assertThat(request.getStreet()).isEqualTo("123 Main St");
        assertThat(request.getCity()).isEqualTo("Test City");
        assertThat(request.getState()).isEqualTo("TS");
        assertThat(request.getZipCode()).isEqualTo("12345");
        assertThat(request.getLandmark()).isEqualTo("Near Park");
        assertThat(request.getIsDefault()).isTrue();
    }

    @Test
    void testAddressResponse() {
        AddressResponse response = AddressResponse.builder()
                .id(1L)
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .landmark("Near Park")
                .isDefault(true)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStreet()).isEqualTo("123 Main St");
        assertThat(response.getCity()).isEqualTo("Test City");
        assertThat(response.getState()).isEqualTo("TS");
        assertThat(response.getZipCode()).isEqualTo("12345");
        assertThat(response.getLandmark()).isEqualTo("Near Park");
        assertThat(response.getIsDefault()).isTrue();
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void testBookingRequest() {
        BookingRequest request = BookingRequest.builder()
                .serviceId(1L)
                .addressId(1L)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(120)
                .couponCode("DISCOUNT10")
                .notes("Test booking")
                .build();

        assertThat(request.getServiceId()).isEqualTo(1L);
        assertThat(request.getAddressId()).isEqualTo(1L);
        assertThat(request.getScheduledAt()).isNotNull();
        assertThat(request.getDurationMinutes()).isEqualTo(120);
        assertThat(request.getCouponCode()).isEqualTo("DISCOUNT10");
        assertThat(request.getNotes()).isEqualTo("Test booking");
    }

    @Test
    void testQuoteRequest() {
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(1L)
                .durationMinutes(120)
                .build();

        assertThat(request.getServiceId()).isEqualTo(1L);
        assertThat(request.getDurationMinutes()).isEqualTo(120);
    }

    @Test
    void testQuoteResponse() {
        QuoteResponse response = QuoteResponse.builder()
                .basePrice(BigDecimal.valueOf(150.00))
                .extraCharge(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .totalPrice(BigDecimal.valueOf(150.00))
                .details("Service quote details")
                .build();

        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getExtraCharge()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getDetails()).isEqualTo("Service quote details");
    }

    @Test
    void testRatingRequest() {
        RatingRequest request = RatingRequest.builder()
                .bookingId(1L)
                .rating(5)
                .comment("Excellent service")
                .build();

        assertThat(request.getBookingId()).isEqualTo(1L);
        assertThat(request.getRating()).isEqualTo(5);
        assertThat(request.getComment()).isEqualTo("Excellent service");
    }

    @Test
    void testRatingResponse() {
        RatingResponse response = RatingResponse.builder()
                .id(1L)
                .customerId(1L)
                .customerName("Customer Name")
                .expertId(2L)
                .expertName("Expert Name")
                .bookingId(1L)
                .rating(5)
                .comment("Excellent service")
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Customer Name");
        assertThat(response.getExpertId()).isEqualTo(2L);
        assertThat(response.getExpertName()).isEqualTo("Expert Name");
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("Excellent service");
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void testTicketRequest() {
        TicketRequest request = TicketRequest.builder()
                .bookingId(1L)
                .subject("Issue subject")
                .description("Issue description")
                .priority("HIGH")
                .build();

        assertThat(request.getBookingId()).isEqualTo(1L);
        assertThat(request.getSubject()).isEqualTo("Issue subject");
        assertThat(request.getDescription()).isEqualTo("Issue description");
        assertThat(request.getPriority()).isEqualTo("HIGH");
    }

    @Test
    void testErrorResponse() {
        ErrorResponse response = ErrorResponse.builder()
                .code("ERROR_CODE")
                .message("Error message")
                .timestamp(LocalDateTime.now())
                .build();

        assertThat(response.getCode()).isEqualTo("ERROR_CODE");
        assertThat(response.getMessage()).isEqualTo("Error message");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void testUpdateProfileRequest() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .fullName("Updated Name")
                .email("updated@example.com")
                .build();

        assertThat(request.getFullName()).isEqualTo("Updated Name");
        assertThat(request.getEmail()).isEqualTo("updated@example.com");
    }
}
