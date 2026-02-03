package com.homesolutions.service.interfaces;

import com.homesolutions.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    UserProfileResponse getProfile(String phone);
    UserProfileResponse updateProfile(String phone, UpdateProfileRequest request);
    AddressResponse createAddress(String phone, AddressRequest request);
    List<AddressResponse> getAddresses(String phone);
    BookingResponse createBooking(String phone, BookingRequest request);
    Page<BookingResponse> getBookings(String phone, Pageable pageable);
    BookingResponse getBookingById(String phone, Long bookingId);
    PaymentResponse createPayment(String phone, PaymentRequest request);
    PaymentResponse confirmPayment(String phone, Long paymentId);
    RatingResponse createRating(String phone, RatingRequest request);
    TicketResponse createTicket(String phone, TicketRequest request);
}
