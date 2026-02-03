package com.homesolutions.mapper;

import com.homesolutions.dto.RatingRequest;
import com.homesolutions.dto.RatingResponse;
import com.homesolutions.entity.Booking;
import com.homesolutions.entity.Rating;
import com.homesolutions.entity.User;

public class RatingMapper {

    private RatingMapper() {
    }

    public static RatingResponse toResponse(Rating rating) {
        if (rating == null) {
            return null;
        }

        Booking booking = rating.getBooking();
        User customer = rating.getCustomer();
        User expert = rating.getExpert();

        return RatingResponse.builder()
                .id(rating.getId())
                .bookingId(booking != null ? booking.getId() : null)
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                .expertId(expert != null ? expert.getId() : null)
                .expertName(expert != null ? expert.getFullName() : null)
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    public static Rating toEntity(RatingRequest request, Booking booking, User customer, User expert) {
        if (request == null) {
            return null;
        }

        return Rating.builder()
                .booking(booking)
                .customer(customer)
                .expert(expert)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }
}
