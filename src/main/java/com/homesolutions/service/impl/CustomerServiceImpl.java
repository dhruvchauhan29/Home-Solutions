package com.homesolutions.service.impl;

import com.homesolutions.dto.*;
import com.homesolutions.entity.*;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.*;
import com.homesolutions.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RatingRepository ratingRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        log.info("Fetching profile for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        user = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", user.getId());
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(String email, AddressRequest request) {
        log.info("Creating address for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Address address = Address.builder()
                .user(user)
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .landmark(request.getLandmark())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        address = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", address.getId());
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String email) {
        log.info("Fetching addresses for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse createBooking(String email, BookingRequest request) {
        log.info("Creating booking for email: {} with serviceId: {}", email, request.getServiceId());

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        com.homesolutions.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getAddressId()));

        if (!address.getUser().getId().equals(customer.getId())) {
            throw new BusinessException("Address does not belong to the customer");
        }

        BigDecimal basePrice = service.getBasePrice();
        BigDecimal extraCharge = BigDecimal.ZERO;

        if (request.getDurationMinutes() > 60) {
            int extraMinutes = request.getDurationMinutes() - 60;
            int extraHours = (int) Math.ceil(extraMinutes / 60.0);
            extraCharge = service.getExtraHourlyRate().multiply(BigDecimal.valueOf(extraHours));
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("NEW50".equalsIgnoreCase(request.getCouponCode())) {
            discount = BigDecimal.valueOf(50);
        }

        BigDecimal totalPrice = basePrice.add(extraCharge).subtract(discount);

        Booking booking = Booking.builder()
                .customer(customer)
                .service(service)
                .address(address)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .totalPrice(totalPrice)
                .couponCode(request.getCouponCode())
                .discount(discount)
                .status(Booking.BookingStatus.PENDING_PAYMENT)
                .notes(request.getNotes())
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", booking.getId());

        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookings(String email, Pageable pageable) {
        log.info("Fetching bookings for email: {}", email);
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId(), pageable)
                .map(this::mapToBookingResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String email, Long bookingId) {
        log.info("Fetching booking ID: {} for email: {}", bookingId, email);
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Booking does not belong to the customer");
        }

        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(String email, PaymentRequest request) {
        log.info("Creating payment for email: {}, bookingId: {}", email, request.getBookingId());

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Booking does not belong to the customer");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING_PAYMENT) {
            throw new BusinessException("Booking is not in PENDING_PAYMENT status");
        }

        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BusinessException("Payment already exists for this booking");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .method(Payment.PaymentMethod.valueOf(request.getMethod().toUpperCase()))
                .status(Payment.PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", payment.getId());

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(String email, Long paymentId) {
        log.info("Confirming payment ID: {} for email: {}", paymentId, email);

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        Booking booking = payment.getBooking();
        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Payment does not belong to the customer");
        }

        if (payment.getStatus() == Payment.PaymentStatus.SUCCEEDED) {
            throw new BusinessException("Payment is already confirmed");
        }

        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
        payment = paymentRepository.save(payment);

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        log.info("Payment confirmed and booking status updated to CONFIRMED");

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public RatingResponse createRating(String email, RatingRequest request) {
        log.info("Creating rating for email: {}, bookingId: {}", email, request.getBookingId());

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Booking does not belong to the customer");
        }

        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new BusinessException("Can only rate completed bookings");
        }

        if (booking.getExpert() == null) {
            throw new BusinessException("No expert assigned to this booking");
        }

        if (ratingRepository.existsByBookingId(booking.getId())) {
            throw new BusinessException("Rating already exists for this booking");
        }

        Rating rating = Rating.builder()
                .booking(booking)
                .customer(customer)
                .expert(booking.getExpert())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        rating = ratingRepository.save(rating);
        log.info("Rating created successfully with ID: {}", rating.getId());

        return mapToRatingResponse(rating);
    }

    @Override
    @Transactional
    public TicketResponse createTicket(String email, TicketRequest request) {
        log.info("Creating ticket for email: {}", email);

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

            if (!booking.getCustomer().getId().equals(customer.getId())) {
                throw new BusinessException("Booking does not belong to the customer");
            }
        }

        Ticket ticket = Ticket.builder()
                .user(customer)
                .booking(booking)
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(Ticket.TicketStatus.OPEN)
                .priority(Ticket.TicketPriority.MEDIUM)
                .build();

        ticket = ticketRepository.save(ticket);
        log.info("Ticket created successfully with ID: {}", ticket.getId());

        return mapToTicketResponse(ticket);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .landmark(address.getLandmark())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFullName())
                .service(mapToServiceResponse(booking.getService()))
                .address(mapToAddressResponse(booking.getAddress()))
                .expertId(booking.getExpert() != null ? booking.getExpert().getId() : null)
                .expertName(booking.getExpert() != null ? booking.getExpert().getFullName() : null)
                .scheduledAt(booking.getScheduledAt())
                .durationMinutes(booking.getDurationMinutes())
                .totalPrice(booking.getTotalPrice())
                .couponCode(booking.getCouponCode())
                .discount(booking.getDiscount())
                .status(booking.getStatus().name())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private ServiceResponse mapToServiceResponse(com.homesolutions.entity.Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(service.getCategory().getId())
                .categoryName(service.getCategory().getName())
                .basePrice(service.getBasePrice())
                .extraHourlyRate(service.getExtraHourlyRate())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .build();
    }



    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private RatingResponse mapToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .bookingId(rating.getBooking().getId())
                .customerId(rating.getCustomer().getId())
                .customerName(rating.getCustomer().getFullName())
                .expertId(rating.getExpert().getId())
                .expertName(rating.getExpert().getFullName())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    private TicketResponse mapToTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .userName(ticket.getUser().getFullName())
                .bookingId(ticket.getBooking() != null ? ticket.getBooking().getId() : null)
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .priority(ticket.getPriority().name())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
