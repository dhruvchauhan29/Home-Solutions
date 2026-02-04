package com.homesolutions;

import com.homesolutions.dto.*;
import com.homesolutions.entity.*;
import com.homesolutions.mapper.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    @Test
    void userMapper_toUserProfileResponse_WithValidUser_ReturnsResponse() {
        // Given
        User user = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("test@example.com")
                .fullName("Test User")
                .roles(Set.of("ROLE_CUSTOMER"))
                .createdAt(LocalDateTime.now())
                .build();

        // When
        UserProfileResponse response = UserMapper.toUserProfileResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPhone()).isEqualTo("1234567890");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getRoles()).contains("ROLE_CUSTOMER");
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void userMapper_toUserProfileResponse_WithNull_ReturnsNull() {
        // When
        UserProfileResponse response = UserMapper.toUserProfileResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void serviceMapper_toResponse_WithValidService_ReturnsResponse() {
        // Given
        Category category = Category.builder()
                .id(1L)
                .name("Plumbing")
                .build();
        Service service = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .category(category)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        ServiceResponse response = ServiceMapper.toResponse(service);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pipe Repair");
        assertThat(response.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Plumbing");
        assertThat(response.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void serviceMapper_toResponse_WithNullCategory_ReturnsResponseWithNullCategoryFields() {
        // Given
        Service service = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .category(null)
                .basePrice(BigDecimal.valueOf(150.00))
                .build();

        // When
        ServiceResponse response = ServiceMapper.toResponse(service);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
    }

    @Test
    void serviceMapper_toResponse_WithNull_ReturnsNull() {
        // When
        ServiceResponse response = ServiceMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void serviceMapper_toEntity_WithValidRequest_ReturnsEntity() {
        // Given
        Category category = Category.builder().id(1L).build();
        ServiceRequest request = ServiceRequest.builder()
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .build();

        // When
        Service service = ServiceMapper.toEntity(request, category);

        // Then
        assertThat(service).isNotNull();
        assertThat(service.getName()).isEqualTo("Pipe Repair");
        assertThat(service.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(service.getCategory()).isEqualTo(category);
        assertThat(service.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void serviceMapper_toEntity_WithNullExtraHourlyRate_UsesDefault() {
        // Given
        Category category = Category.builder().id(1L).build();
        ServiceRequest request = ServiceRequest.builder()
                .name("Pipe Repair")
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(null)
                .build();

        // When
        Service service = ServiceMapper.toEntity(request, category);

        // Then
        assertThat(service).isNotNull();
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void serviceMapper_toEntity_WithNull_ReturnsNull() {
        // When
        Service service = ServiceMapper.toEntity(null, null);

        // Then
        assertThat(service).isNull();
    }

    @Test
    void serviceMapper_updateEntityFromRequest_UpdatesAllFields() {
        // Given
        Category oldCategory = Category.builder().id(1L).build();
        Category newCategory = Category.builder().id(2L).build();
        Service service = Service.builder()
                .name("Old Name")
                .description("Old Description")
                .category(oldCategory)
                .basePrice(BigDecimal.valueOf(100.00))
                .extraHourlyRate(BigDecimal.valueOf(50.00))
                .build();
        ServiceRequest request = ServiceRequest.builder()
                .name("New Name")
                .description("New Description")
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .build();

        // When
        ServiceMapper.updateEntityFromRequest(service, request, newCategory);

        // Then
        assertThat(service.getName()).isEqualTo("New Name");
        assertThat(service.getDescription()).isEqualTo("New Description");
        assertThat(service.getCategory()).isEqualTo(newCategory);
        assertThat(service.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void serviceMapper_updateEntityFromRequest_WithNullFields_DoesNotUpdate() {
        // Given
        Category category = Category.builder().id(1L).build();
        Service service = Service.builder()
                .name("Original Name")
                .description("Original Description")
                .category(category)
                .basePrice(BigDecimal.valueOf(100.00))
                .extraHourlyRate(BigDecimal.valueOf(50.00))
                .build();
        ServiceRequest request = ServiceRequest.builder()
                .name(null)
                .description(null)
                .basePrice(null)
                .extraHourlyRate(null)
                .build();

        // When
        ServiceMapper.updateEntityFromRequest(service, request, null);

        // Then
        assertThat(service.getName()).isEqualTo("Original Name");
        assertThat(service.getDescription()).isEqualTo("Original Description");
        assertThat(service.getCategory()).isEqualTo(category);
        assertThat(service.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    @Test
    void serviceMapper_updateEntityFromRequest_WithNullServiceOrRequest_DoesNothing() {
        // Given
        ServiceRequest request = ServiceRequest.builder().build();
        Service service = Service.builder().build();

        // When/Then - should not throw exception
        ServiceMapper.updateEntityFromRequest(null, request, null);
        ServiceMapper.updateEntityFromRequest(service, null, null);
        ServiceMapper.updateEntityFromRequest(null, null, null);
    }

    @Test
    void categoryMapper_toResponse_WithValidCategory_ReturnsResponse() {
        // Given
        Category category = Category.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        // When
        CategoryResponse response = CategoryMapper.toResponse(category);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Plumbing");
        assertThat(response.getDescription()).isEqualTo("Plumbing services");
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void categoryMapper_toResponse_WithNull_ReturnsNull() {
        // When
        CategoryResponse response = CategoryMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void addressMapper_toResponse_WithValidAddress_ReturnsResponse() {
        // Given
        User user = User.builder().id(1L).build();
        Address address = Address.builder()
                .id(1L)
                .user(user)
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .landmark("Near Park")
                .isDefault(true)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        AddressResponse response = AddressMapper.toResponse(address);

        // Then
        assertThat(response).isNotNull();
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
    void addressMapper_toResponse_WithNull_ReturnsNull() {
        // When
        AddressResponse response = AddressMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void ratingMapper_toResponse_WithValidRating_ReturnsResponse() {
        // Given
        User customer = User.builder().id(1L).fullName("Customer Name").build();
        User expert = User.builder().id(2L).fullName("Expert Name").build();
        Booking booking = Booking.builder().id(1L).build();
        Rating rating = Rating.builder()
                .id(1L)
                .customer(customer)
                .expert(expert)
                .booking(booking)
                .rating(5)
                .comment("Excellent service")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        RatingResponse response = RatingMapper.toResponse(rating);

        // Then
        assertThat(response).isNotNull();
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
    void ratingMapper_toResponse_WithNull_ReturnsNull() {
        // When
        RatingResponse response = RatingMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void paymentMapper_toResponse_WithValidPayment_ReturnsResponse() {
        // Given
        Booking booking = Booking.builder().id(1L).build();
        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .amount(BigDecimal.valueOf(150.00))
                .method(Payment.PaymentMethod.CARD)
                .status(Payment.PaymentStatus.SUCCEEDED)
                .transactionId("TXN123456")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        PaymentResponse response = PaymentMapper.toResponse(payment);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getMethod()).isEqualTo("CARD");
        assertThat(response.getStatus()).isEqualTo("SUCCEEDED");
        assertThat(response.getTransactionId()).isEqualTo("TXN123456");
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void paymentMapper_toResponse_WithNull_ReturnsNull() {
        // When
        PaymentResponse response = PaymentMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void bookingMapper_toResponse_WithValidBooking_ReturnsResponse() {
        // Given
        User customer = User.builder().id(1L).fullName("Customer Name").build();
        User expert = User.builder().id(2L).fullName("Expert Name").build();
        Category category = Category.builder().id(1L).name("Plumbing").build();
        Service service = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .category(category)
                .basePrice(BigDecimal.valueOf(150.00))
                .build();
        Address address = Address.builder()
                .id(1L)
                .street("123 Main St")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .customer(customer)
                .expert(expert)
                .service(service)
                .address(address)
                .scheduledAt(LocalDateTime.now())
                .durationMinutes(120)
                .totalPrice(BigDecimal.valueOf(150.00))
                .couponCode("DISCOUNT10")
                .discount(BigDecimal.valueOf(15.00))
                .status(Booking.BookingStatus.CONFIRMED)
                .notes("Test notes")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        BookingResponse response = BookingMapper.toResponse(booking);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Customer Name");
        assertThat(response.getExpertId()).isEqualTo(2L);
        assertThat(response.getExpertName()).isEqualTo("Expert Name");
        assertThat(response.getService()).isNotNull();
        assertThat(response.getAddress()).isNotNull();
        assertThat(response.getDurationMinutes()).isEqualTo(120);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(response.getCouponCode()).isEqualTo("DISCOUNT10");
        assertThat(response.getDiscount()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void bookingMapper_toResponse_WithNull_ReturnsNull() {
        // When
        BookingResponse response = BookingMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void ticketMapper_toResponse_WithValidTicket_ReturnsResponse() {
        // Given
        User user = User.builder().id(1L).fullName("Test User").build();
        Booking booking = Booking.builder().id(1L).build();
        Ticket ticket = Ticket.builder()
                .id(1L)
                .user(user)
                .booking(booking)
                .subject("Issue subject")
                .description("Issue description")
                .status(Ticket.TicketStatus.OPEN)
                .priority(Ticket.TicketPriority.HIGH)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        TicketResponse response = TicketMapper.toResponse(ticket);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUserName()).isEqualTo("Test User");
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getSubject()).isEqualTo("Issue subject");
        assertThat(response.getDescription()).isEqualTo("Issue description");
        assertThat(response.getStatus()).isEqualTo("OPEN");
        assertThat(response.getPriority()).isEqualTo("HIGH");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void ticketMapper_toResponse_WithNull_ReturnsNull() {
        // When
        TicketResponse response = TicketMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void categoryMapper_toEntity_WithValidRequest_ReturnsEntity() {
        // Given
        CategoryRequest request = CategoryRequest.builder()
                .name("Plumbing")
                .description("Plumbing services")
                .build();

        // When
        Category category = CategoryMapper.toEntity(request);

        // Then
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Plumbing");
        assertThat(category.getDescription()).isEqualTo("Plumbing services");
    }

    @Test
    void categoryMapper_toEntity_WithNull_ReturnsNull() {
        // When
        Category category = CategoryMapper.toEntity(null);

        // Then
        assertThat(category).isNull();
    }

    @Test
    void categoryMapper_updateEntityFromRequest_UpdatesAllFields() {
        // Given
        Category category = Category.builder()
                .name("Old Name")
                .description("Old Description")
                .build();
        CategoryRequest request = CategoryRequest.builder()
                .name("New Name")
                .description("New Description")
                .build();

        // When
        CategoryMapper.updateEntityFromRequest(category, request);

        // Then
        assertThat(category.getName()).isEqualTo("New Name");
        assertThat(category.getDescription()).isEqualTo("New Description");
    }

    @Test
    void categoryMapper_updateEntityFromRequest_WithNullFields_DoesNotUpdate() {
        // Given
        Category category = Category.builder()
                .name("Original Name")
                .description("Original Description")
                .build();
        CategoryRequest request = CategoryRequest.builder()
                .name(null)
                .description(null)
                .build();

        // When
        CategoryMapper.updateEntityFromRequest(category, request);

        // Then
        assertThat(category.getName()).isEqualTo("Original Name");
        assertThat(category.getDescription()).isEqualTo("Original Description");
    }

    @Test
    void categoryMapper_updateEntityFromRequest_WithNullCategoryOrRequest_DoesNothing() {
        // When/Then - should not throw exception
        CategoryMapper.updateEntityFromRequest(null, CategoryRequest.builder().build());
        CategoryMapper.updateEntityFromRequest(Category.builder().build(), null);
        CategoryMapper.updateEntityFromRequest(null, null);
    }

    @Test
    void addressMapper_toEntity_WithValidRequest_ReturnsEntity() {
        // Given
        User user = User.builder().id(1L).build();
        AddressRequest request = AddressRequest.builder()
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .landmark("Near Park")
                .isDefault(true)
                .build();

        // When
        Address address = AddressMapper.toEntity(request, user);

        // Then
        assertThat(address).isNotNull();
        assertThat(address.getUser()).isEqualTo(user);
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("Test City");
        assertThat(address.getState()).isEqualTo("TS");
        assertThat(address.getZipCode()).isEqualTo("12345");
        assertThat(address.getLandmark()).isEqualTo("Near Park");
        assertThat(address.getIsDefault()).isTrue();
    }

    @Test
    void addressMapper_toEntity_WithNullIsDefault_UsesFalse() {
        // Given
        User user = User.builder().id(1L).build();
        AddressRequest request = AddressRequest.builder()
                .street("123 Main St")
                .isDefault(null)
                .build();

        // When
        Address address = AddressMapper.toEntity(request, user);

        // Then
        assertThat(address).isNotNull();
        assertThat(address.getIsDefault()).isFalse();
    }

    @Test
    void addressMapper_toEntity_WithNull_ReturnsNull() {
        // When
        Address address = AddressMapper.toEntity(null, null);

        // Then
        assertThat(address).isNull();
    }

    @Test
    void addressMapper_updateEntityFromRequest_UpdatesAllFields() {
        // Given
        User user = User.builder().id(1L).build();
        Address address = Address.builder()
                .user(user)
                .street("Old Street")
                .city("Old City")
                .state("OS")
                .zipCode("00000")
                .landmark("Old Landmark")
                .isDefault(false)
                .build();
        AddressRequest request = AddressRequest.builder()
                .street("New Street")
                .city("New City")
                .state("NS")
                .zipCode("11111")
                .landmark("New Landmark")
                .isDefault(true)
                .build();

        // When
        AddressMapper.updateEntityFromRequest(address, request);

        // Then
        assertThat(address.getStreet()).isEqualTo("New Street");
        assertThat(address.getCity()).isEqualTo("New City");
        assertThat(address.getState()).isEqualTo("NS");
        assertThat(address.getZipCode()).isEqualTo("11111");
        assertThat(address.getLandmark()).isEqualTo("New Landmark");
        assertThat(address.getIsDefault()).isTrue();
    }

    @Test
    void addressMapper_updateEntityFromRequest_WithNullFields_DoesNotUpdate() {
        // Given
        Address address = Address.builder()
                .street("Original Street")
                .city("Original City")
                .state("OS")
                .zipCode("00000")
                .landmark("Original Landmark")
                .isDefault(false)
                .build();
        AddressRequest request = AddressRequest.builder()
                .street(null)
                .city(null)
                .state(null)
                .zipCode(null)
                .landmark(null)
                .isDefault(null)
                .build();

        // When
        AddressMapper.updateEntityFromRequest(address, request);

        // Then
        assertThat(address.getStreet()).isEqualTo("Original Street");
        assertThat(address.getCity()).isEqualTo("Original City");
        assertThat(address.getState()).isEqualTo("OS");
        assertThat(address.getZipCode()).isEqualTo("00000");
        assertThat(address.getLandmark()).isEqualTo("Original Landmark");
        assertThat(address.getIsDefault()).isFalse();
    }

    @Test
    void addressMapper_updateEntityFromRequest_WithNullAddressOrRequest_DoesNothing() {
        // When/Then - should not throw exception
        AddressMapper.updateEntityFromRequest(null, AddressRequest.builder().build());
        AddressMapper.updateEntityFromRequest(Address.builder().build(), null);
        AddressMapper.updateEntityFromRequest(null, null);
    }

    @Test
    void bookingMapper_toEntity_WithValidRequest_ReturnsEntity() {
        // Given
        User customer = User.builder().id(1L).build();
        Category category = Category.builder().id(1L).build();
        Service service = Service.builder().id(1L).category(category).build();
        Address address = Address.builder().id(1L).build();
        BookingRequest request = BookingRequest.builder()
                .serviceId(1L)
                .addressId(1L)
                .scheduledAt(LocalDateTime.now())
                .durationMinutes(120)
                .couponCode("DISCOUNT10")
                .notes("Test notes")
                .build();

        // When
        Booking booking = BookingMapper.toEntity(request, customer, service, address);

        // Then
        assertThat(booking).isNotNull();
        assertThat(booking.getCustomer()).isEqualTo(customer);
        assertThat(booking.getService()).isEqualTo(service);
        assertThat(booking.getAddress()).isEqualTo(address);
        assertThat(booking.getScheduledAt()).isNotNull();
        assertThat(booking.getDurationMinutes()).isEqualTo(120);
        assertThat(booking.getCouponCode()).isEqualTo("DISCOUNT10");
        assertThat(booking.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void bookingMapper_toEntity_WithNull_ReturnsNull() {
        // When
        Booking booking = BookingMapper.toEntity(null, null, null, null);

        // Then
        assertThat(booking).isNull();
    }

    @Test
    void bookingMapper_updateEntityFromRequest_UpdatesAllFields() {
        // Given
        Category oldCategory = Category.builder().id(1L).build();
        Service oldService = Service.builder().id(1L).category(oldCategory).build();
        Address oldAddress = Address.builder().id(1L).build();
        Booking booking = Booking.builder()
                .service(oldService)
                .address(oldAddress)
                .scheduledAt(LocalDateTime.now().minusDays(1))
                .durationMinutes(60)
                .couponCode("OLD_CODE")
                .notes("Old notes")
                .build();

        Category newCategory = Category.builder().id(2L).build();
        Service newService = Service.builder().id(2L).category(newCategory).build();
        Address newAddress = Address.builder().id(2L).build();
        BookingRequest request = BookingRequest.builder()
                .scheduledAt(LocalDateTime.now())
                .durationMinutes(120)
                .couponCode("NEW_CODE")
                .notes("New notes")
                .build();

        // When
        BookingMapper.updateEntityFromRequest(booking, request, newService, newAddress);

        // Then
        assertThat(booking.getService()).isEqualTo(newService);
        assertThat(booking.getAddress()).isEqualTo(newAddress);
        assertThat(booking.getDurationMinutes()).isEqualTo(120);
        assertThat(booking.getCouponCode()).isEqualTo("NEW_CODE");
        assertThat(booking.getNotes()).isEqualTo("New notes");
    }

    @Test
    void bookingMapper_updateEntityFromRequest_WithNullFields_DoesNotUpdate() {
        // Given
        Category category = Category.builder().id(1L).build();
        Service service = Service.builder().id(1L).category(category).build();
        Address address = Address.builder().id(1L).build();
        LocalDateTime originalTime = LocalDateTime.now();
        Booking booking = Booking.builder()
                .service(service)
                .address(address)
                .scheduledAt(originalTime)
                .durationMinutes(60)
                .couponCode("ORIGINAL_CODE")
                .notes("Original notes")
                .build();

        BookingRequest request = BookingRequest.builder()
                .scheduledAt(null)
                .durationMinutes(null)
                .couponCode(null)
                .notes(null)
                .build();

        // When
        BookingMapper.updateEntityFromRequest(booking, request, null, null);

        // Then
        assertThat(booking.getService()).isEqualTo(service);
        assertThat(booking.getAddress()).isEqualTo(address);
        assertThat(booking.getScheduledAt()).isEqualTo(originalTime);
        assertThat(booking.getDurationMinutes()).isEqualTo(60);
        assertThat(booking.getCouponCode()).isEqualTo("ORIGINAL_CODE");
        assertThat(booking.getNotes()).isEqualTo("Original notes");
    }

    @Test
    void bookingMapper_updateEntityFromRequest_WithNullBookingOrRequest_DoesNothing() {
        // When/Then - should not throw exception
        BookingMapper.updateEntityFromRequest(null, BookingRequest.builder().build(), null, null);
        BookingMapper.updateEntityFromRequest(Booking.builder().build(), null, null, null);
        BookingMapper.updateEntityFromRequest(null, null, null, null);
    }
}
