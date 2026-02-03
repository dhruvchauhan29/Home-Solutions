package com.homesolutions.mapper;

import com.homesolutions.dto.PaymentRequest;
import com.homesolutions.dto.PaymentResponse;
import com.homesolutions.entity.Booking;
import com.homesolutions.entity.Payment;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        Booking booking = payment.getBooking();

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(booking != null ? booking.getId() : null)
                .amount(payment.getAmount())
                .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public static Payment toEntity(PaymentRequest request, Booking booking) {
        if (request == null) {
            return null;
        }

        Payment.PaymentMethod method = null;
        if (request.getMethod() != null) {
            try {
                method = Payment.PaymentMethod.valueOf(request.getMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                method = Payment.PaymentMethod.CARD;
            }
        }

        return Payment.builder()
                .booking(booking)
                .amount(booking != null ? booking.getTotalPrice() : null)
                .method(method)
                .build();
    }
}
