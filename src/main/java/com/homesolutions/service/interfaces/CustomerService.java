package com.homesolutions.service.interfaces;

import com.homesolutions.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
    AddressResponse createAddress(String email, AddressRequest request);
    List<AddressResponse> getAddresses(String email);
    BookingResponse createBooking(String email, BookingRequest request);
    Page<BookingResponse> getBookings(String email, Pageable pageable);
    BookingResponse getBookingById(String email, Long bookingId);
    PaymentResponse createPayment(String email, PaymentRequest request);
    PaymentResponse confirmPayment(String email, Long paymentId);
    RatingResponse createRating(String email, RatingRequest request);
    TicketResponse createTicket(String email, TicketRequest request);
}
