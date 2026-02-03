package com.homesolutions.controller;

import com.homesolutions.dto.*;
import com.homesolutions.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Customer", description = "Customer-specific endpoints")
public class CustomerController {

    private final CustomerService customerService;

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/profile")
    @Operation(summary = "Get customer profile", description = "Get the authenticated customer's profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        String email = getAuthenticatedEmail();
        log.info("Get profile for email: {}", email);
        UserProfileResponse profile = customerService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update customer profile", description = "Update the authenticated customer's profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Update profile for email: {}", email);
        UserProfileResponse profile = customerService.updateProfile(email, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get customer addresses", description = "Get all addresses for the authenticated customer")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        String email = getAuthenticatedEmail();
        log.info("Get addresses for email: {}", email);
        List<AddressResponse> addresses = customerService.getAddresses(email);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/addresses")
    @Operation(summary = "Create customer address", description = "Create a new address for the authenticated customer")
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Create address for email: {}", email);
        AddressResponse address = customerService.createAddress(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @PostMapping("/bookings")
    @Operation(summary = "Create booking", description = "Create a new booking for the authenticated customer")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Create booking for email: {}", email);
        BookingResponse booking = customerService.createBooking(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get customer bookings", description = "Get all bookings for the authenticated customer")
    public ResponseEntity<Page<BookingResponse>> getBookings(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        String email = getAuthenticatedEmail();
        log.info("Get bookings for email: {}, page: {}, size: {}", email, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> bookings = customerService.getBookings(email, pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{id}")
    @Operation(summary = "Get booking by ID", description = "Get a specific booking by ID for the authenticated customer")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        log.info("Get booking {} for email: {}", id, email);
        BookingResponse booking = customerService.getBookingById(email, id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/payments")
    @Operation(summary = "Create payment", description = "Create a payment for the authenticated customer")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Create payment for email: {}", email);
        PaymentResponse payment = customerService.createPayment(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID", description = "Get a specific payment by ID")
    public ResponseEntity<Map<String, String>> getPaymentById(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        log.info("Get payment {} for email: {}", id, email);
        return ResponseEntity.ok(Map.of(
            "message", "Payment retrieval endpoint",
            "paymentId", id.toString()
        ));
    }

    @PostMapping("/payments/{id}/confirm")
    @Operation(summary = "Confirm payment", description = "Confirm a payment for the authenticated customer")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        log.info("Confirm payment {} for email: {}", id, email);
        PaymentResponse payment = customerService.confirmPayment(email, id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/bookings/{id}/receipt")
    @Operation(summary = "Get booking receipt", description = "Get receipt for a specific booking")
    public ResponseEntity<Map<String, String>> getBookingReceipt(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        log.info("Get receipt for booking {} for email: {}", id, email);
        return ResponseEntity.ok(Map.of(
            "message", "Receipt retrieval endpoint",
            "bookingId", id.toString()
        ));
    }

    @PostMapping("/bookings/{id}/rating")
    @Operation(summary = "Rate booking", description = "Submit a rating for a completed booking")
    public ResponseEntity<RatingResponse> rateBooking(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Rate booking {} for email: {}", id, email);
        RatingResponse rating = customerService.createRating(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rating);
    }

    @PostMapping("/tickets")
    @Operation(summary = "Create support ticket", description = "Create a support ticket")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest request) {
        String email = getAuthenticatedEmail();
        log.info("Create ticket for email: {}", email);
        TicketResponse ticket = customerService.createTicket(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
