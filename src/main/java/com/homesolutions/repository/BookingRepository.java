package com.homesolutions.repository;

import com.homesolutions.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    Page<Booking> findByExpertIdOrderByCreatedAtDesc(Long expertId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.createdAt < :cutoffTime")
    List<Booking> findOldUnpaidBookings(
        @Param("status") Booking.BookingStatus status,
        @Param("cutoffTime") LocalDateTime cutoffTime
    );
}
