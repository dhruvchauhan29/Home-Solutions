package com.homesolutions;

import com.homesolutions.entity.Booking;
import com.homesolutions.repository.BookingRepository;
import com.homesolutions.util.BookingCleanupScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingCleanupSchedulerTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "unpaidMinutes", 30);
    }

    @Test
    void cleanupUnpaidBookings_WithUnpaidBookings_CancelsAndSavesBookings() {
        // Given
        Booking unpaidBooking1 = Booking.builder()
                .id(1L)
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.now().minusMinutes(60))
                .build();
        Booking unpaidBooking2 = Booking.builder()
                .id(2L)
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.now().minusMinutes(45))
                .build();
        List<Booking> unpaidBookings = List.of(unpaidBooking1, unpaidBooking2);

        when(bookingRepository.findOldUnpaidBookings(
                eq(Booking.BookingStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).thenReturn(unpaidBookings);

        // When
        scheduler.cleanupUnpaidBookings();

        // Then
        verify(bookingRepository, times(1)).findOldUnpaidBookings(
                eq(Booking.BookingStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        );
        verify(bookingRepository, times(2)).save(any(Booking.class));
        assert unpaidBooking1.getStatus() == Booking.BookingStatus.CANCELLED;
        assert unpaidBooking2.getStatus() == Booking.BookingStatus.CANCELLED;
    }

    @Test
    void cleanupUnpaidBookings_WithNoUnpaidBookings_DoesNotSaveAnything() {
        // Given
        when(bookingRepository.findOldUnpaidBookings(
                eq(Booking.BookingStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // When
        scheduler.cleanupUnpaidBookings();

        // Then
        verify(bookingRepository, times(1)).findOldUnpaidBookings(
                eq(Booking.BookingStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        );
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
