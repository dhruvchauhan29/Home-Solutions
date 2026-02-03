package com.homesolutions;

import com.homesolutions.dto.BookingResponse;
import com.homesolutions.entity.*;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.BookingRepository;
import com.homesolutions.repository.TicketRepository;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.service.impl.ExpertServiceImpl;
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
class ExpertServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ExpertServiceImpl expertService;

    private User mockExpert;
    private User mockCustomer;
    private Booking mockBooking;
    private Category mockCategory;
    private Service mockService;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        Set<String> expertRoles = new HashSet<>();
        expertRoles.add("ROLE_EXPERT");

        mockExpert = User.builder()
                .id(2L)
                .phone("9876543210")
                .email("expert@test.com")
                .fullName("Test Expert")
                .password("encodedPassword")
                .roles(expertRoles)
                .enabled(true)
                .build();

        Set<String> customerRoles = new HashSet<>();
        customerRoles.add("ROLE_CUSTOMER");

        mockCustomer = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("customer@test.com")
                .fullName("Test Customer")
                .password("encodedPassword")
                .roles(customerRoles)
                .enabled(true)
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
                .description("Fix leaking pipes")
                .category(mockCategory)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .build();

        mockAddress = Address.builder()
                .id(1L)
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .isDefault(true)
                .user(mockCustomer)
                .build();

        mockBooking = Booking.builder()
                .id(1L)
                .customer(mockCustomer)
                .service(mockService)
                .address(mockAddress)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(120)
                .totalPrice(BigDecimal.valueOf(150.00))
                .status(Booking.BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testAcceptJob() {
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = expertService.acceptJob("expert@test.com", 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testAcceptJob_ExpertNotFound() {
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expertService.acceptJob("expert@test.com", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: expert@test.com");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository, never()).findById(any());
    }

    @Test
    void testAcceptJob_NotAnExpert() {
        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(mockCustomer));

        assertThatThrownBy(() -> expertService.acceptJob("customer@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User is not an expert");

        verify(userRepository).findByEmail("customer@test.com");
        verify(bookingRepository, never()).findById(any());
    }

    @Test
    void testAcceptJob_BookingNotFound() {
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expertService.acceptJob("expert@test.com", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found with ID: 1");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
    }

    @Test
    void testAcceptJob_InvalidStatus() {
        mockBooking.setStatus(Booking.BookingStatus.COMPLETED);
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.acceptJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not in CONFIRMED status");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testDeclineJob() {
        mockBooking.setExpert(mockExpert);
        mockBooking.setStatus(Booking.BookingStatus.ASSIGNED);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = expertService.declineJob("expert@test.com", 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testDeclineJob_NotAssignedToExpert() {
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.declineJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not assigned to this expert");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testStartJob() {
        mockBooking.setExpert(mockExpert);
        mockBooking.setStatus(Booking.BookingStatus.ASSIGNED);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = expertService.startJob("expert@test.com", 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testStartJob_NotAssignedToExpert() {
        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.startJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not assigned to this expert");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testStartJob_InvalidStatus() {
        mockBooking.setExpert(mockExpert);
        mockBooking.setStatus(Booking.BookingStatus.COMPLETED);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.startJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not in ASSIGNED status");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCompleteJob() {
        mockBooking.setExpert(mockExpert);
        mockBooking.setStatus(Booking.BookingStatus.IN_PROGRESS);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = expertService.completeJob("expert@test.com", 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCompleteJob_NotAssignedToExpert() {
        mockBooking.setStatus(Booking.BookingStatus.IN_PROGRESS);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.completeJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not assigned to this expert");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCompleteJob_InvalidStatus() {
        mockBooking.setExpert(mockExpert);
        mockBooking.setStatus(Booking.BookingStatus.ASSIGNED);

        when(userRepository.findByEmail("expert@test.com")).thenReturn(Optional.of(mockExpert));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThatThrownBy(() -> expertService.completeJob("expert@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Booking is not in IN_PROGRESS status");

        verify(userRepository).findByEmail("expert@test.com");
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }
}
