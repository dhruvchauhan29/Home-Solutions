package com.homesolutions;

import com.homesolutions.dto.*;
import com.homesolutions.entity.*;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.*;
import com.homesolutions.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User mockCustomer;
    private Address mockAddress;
    private Service mockService;
    private Category mockCategory;
    private Booking mockBooking;
    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        mockCustomer = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("encodedPassword")
                .roles(roles)
                .enabled(true)
                .build();

        mockAddress = Address.builder()
                .id(1L)
                .user(mockCustomer)
                .street("123 Main St")
                .city("Mumbai")
                .state("Maharashtra")
                .zipCode("400001")
                .landmark("Near Central Park")
                .isDefault(true)
                .build();

        mockCategory = Category.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        mockService = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Professional pipe repair")
                .category(mockCategory)
                .basePrice(BigDecimal.valueOf(500.00))
                .extraHourlyRate(BigDecimal.valueOf(100.00))
                .active(true)
                .build();

        mockBooking = Booking.builder()
                .id(1L)
                .customer(mockCustomer)
                .service(mockService)
                .address(mockAddress)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .totalPrice(BigDecimal.valueOf(500.00))
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .build();

        mockPayment = Payment.builder()
                .id(1L)
                .booking(mockBooking)
                .amount(BigDecimal.valueOf(500.00))
                .method(Payment.PaymentMethod.CARD)
                .status(Payment.PaymentStatus.PENDING)
                .transactionId("TXN123456")
                .build();
    }

    @Test
    void testGetProfile_Success() {
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));

        UserProfileResponse response = customerService.getProfile("1234567890");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPhone()).isEqualTo("1234567890");
        assertThat(response.getEmail()).isEqualTo("customer@test.com");
        assertThat(response.getFullName()).isEqualTo("Test Customer");

        verify(userRepository).findByPhone("1234567890");
    }

    @Test
    void testUpdateProfile_Success() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .email("newemail@test.com")
                .fullName("Updated Customer")
                .build();

        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));
        when(userRepository.existsByEmail("newemail@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockCustomer);

        UserProfileResponse response = customerService.updateProfile("1234567890", request);

        assertThat(response).isNotNull();
        verify(userRepository).findByPhone("1234567890");
        verify(userRepository).existsByEmail("newemail@test.com");
        verify(userRepository).save(mockCustomer);
    }

    @Test
    void testCreateAddress_Success() {
        AddressRequest request = AddressRequest.builder()
                .street("456 New St")
                .city("Delhi")
                .state("Delhi")
                .zipCode("110001")
                .landmark("Near Metro")
                .isDefault(false)
                .build();

        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

        AddressResponse response = customerService.createAddress("1234567890", request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStreet()).isEqualTo("123 Main St");
        assertThat(response.getCity()).isEqualTo("Mumbai");

        verify(userRepository).findByPhone("1234567890");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateBooking_Success() {
        BookingRequest request = BookingRequest.builder()
                .serviceId(1L)
                .addressId(1L)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(60)
                .notes("Please call before arriving")
                .build();

        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = customerService.createBooking("1234567890", request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));

        verify(userRepository).findByPhone("1234567890");
        verify(serviceRepository).findById(1L);
        verify(addressRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreatePayment_Success() {
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(1L)
                .method("CARD")
                .build();

        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        PaymentResponse response = customerService.createPayment("1234567890", request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(response.getMethod()).isEqualTo("CARD");
        assertThat(response.getStatus()).isEqualTo("PENDING");

        verify(userRepository).findByPhone("1234567890");
        verify(bookingRepository).findById(1L);
        verify(paymentRepository).findByBookingId(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testConfirmPayment_Success() {
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(mockCustomer));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        PaymentResponse response = customerService.confirmPayment("1234567890", 1L);

        assertThat(response).isNotNull();
        verify(userRepository).findByPhone("1234567890");
        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(mockPayment);
        verify(bookingRepository).save(mockBooking);
    }
}
