package com.homesolutions.repository;

import com.homesolutions.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);
}
