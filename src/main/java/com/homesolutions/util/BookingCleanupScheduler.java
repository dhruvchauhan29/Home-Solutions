package com.homesolutions.util;

import com.homesolutions.entity.Booking;
import com.homesolutions.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;

    @Value("${booking.cleanup.unpaid-minutes:30}")
    private int unpaidMinutes;

    @Scheduled(cron = "${booking.cleanup.cron:0 */10 * * * *}")
    @Transactional
    public void cleanupUnpaidBookings() {
        log.info("Starting cleanup of unpaid bookings older than {} minutes", unpaidMinutes);
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(unpaidMinutes);
        List<Booking> unpaidBookings = bookingRepository.findOldUnpaidBookings(
            Booking.BookingStatus.PENDING_PAYMENT, 
            cutoffTime
        );
        
        if (unpaidBookings.isEmpty()) {
            log.debug("No unpaid bookings found to cleanup");
            return;
        }
        
        for (Booking booking : unpaidBookings) {
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            log.info("Cancelled unpaid booking ID: {} created at {}", booking.getId(), booking.getCreatedAt());
        }
        
        log.info("Cleanup completed. Cancelled {} unpaid bookings", unpaidBookings.size());
    }
}
