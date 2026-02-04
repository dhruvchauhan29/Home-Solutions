package com.homesolutions.service.impl;

import com.homesolutions.dto.BookingResponse;
import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.dto.TicketResponse;
import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.dto.AddressResponse;
import com.homesolutions.entity.Address;
import com.homesolutions.entity.Booking;
import com.homesolutions.entity.Category;
import com.homesolutions.entity.Ticket;
import com.homesolutions.entity.User;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.BookingRepository;
import com.homesolutions.repository.TicketRepository;
import com.homesolutions.repository.UserRepository;
import com.homesolutions.service.interfaces.ExpertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpertServiceImpl implements ExpertService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public UserProfileResponse onboard(String email, String details) {
        log.info("Onboarding expert with email: {}", email);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        expert.setEnabled(true);
        expert = userRepository.save(expert);

        log.info("Expert onboarded successfully: {}", expert.getId());
        return mapToUserProfileResponse(expert);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getJobs(String email, Pageable pageable) {
        log.info("Fetching jobs for expert with email: {}", email);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        return bookingRepository.findByExpertIdOrderByCreatedAtDesc(expert.getId(), pageable)
                .map(this::mapToBookingResponse);
    }

    @Override
    @Transactional
    public BookingResponse acceptJob(String email, Long bookingId) {
        log.info("Expert {} accepting job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new BusinessException("Booking is not in CONFIRMED status");
        }

        booking.setExpert(expert);
        booking.setStatus(Booking.BookingStatus.ASSIGNED);
        booking = bookingRepository.save(booking);

        log.info("Job {} accepted by expert {}", bookingId, expert.getId());
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse declineJob(String email, Long bookingId) {
        log.info("Expert {} declining job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getExpert() == null || !booking.getExpert().getId().equals(expert.getId())) {
            throw new BusinessException("Booking is not assigned to this expert");
        }

        booking.setExpert(null);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        log.info("Job {} declined by expert {}", bookingId, expert.getId());
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse arrivedAtJob(String email, Long bookingId) {
        log.info("Expert {} arrived at job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getExpert() == null || !booking.getExpert().getId().equals(expert.getId())) {
            throw new BusinessException("Booking is not assigned to this expert");
        }

        if (booking.getStatus() != Booking.BookingStatus.ASSIGNED) {
            throw new BusinessException("Booking is not in ASSIGNED status");
        }

        log.info("Expert {} arrived at job {}", expert.getId(), bookingId);
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse startJob(String email, Long bookingId) {
        log.info("Expert {} starting job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getExpert() == null || !booking.getExpert().getId().equals(expert.getId())) {
            throw new BusinessException("Booking is not assigned to this expert");
        }

        if (booking.getStatus() != Booking.BookingStatus.ASSIGNED) {
            throw new BusinessException("Booking is not in ASSIGNED status");
        }

        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking = bookingRepository.save(booking);

        log.info("Job {} started by expert {}", bookingId, expert.getId());
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse completeJob(String email, Long bookingId) {
        log.info("Expert {} completing job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getExpert() == null || !booking.getExpert().getId().equals(expert.getId())) {
            throw new BusinessException("Booking is not assigned to this expert");
        }

        if (booking.getStatus() != Booking.BookingStatus.IN_PROGRESS) {
            throw new BusinessException("Booking is not in IN_PROGRESS status");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);

        log.info("Job {} completed by expert {}", bookingId, expert.getId());
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public TicketResponse reportIssue(String email, Long bookingId, String issue) {
        log.info("Expert {} reporting issue for job {}", email, bookingId);

        User expert = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!expert.getRoles().contains("ROLE_EXPERT")) {
            throw new BusinessException("User is not an expert");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getExpert() == null || !booking.getExpert().getId().equals(expert.getId())) {
            throw new BusinessException("Booking is not assigned to this expert");
        }

        Ticket ticket = Ticket.builder()
                .user(expert)
                .booking(booking)
                .subject("Expert Issue Report - Booking #" + bookingId)
                .description(issue)
                .status(Ticket.TicketStatus.OPEN)
                .priority(Ticket.TicketPriority.HIGH)
                .build();

        ticket = ticketRepository.save(ticket);

        log.info("Issue reported successfully with ticket ID: {}", ticket.getId());
        return mapToTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingConfirmedBookingsForExpert() {
        log.info("Fetching pending confirmed bookings for experts");

        List<Booking> bookings = bookingRepository.findByStatusAndExpertIsNull(Booking.BookingStatus.CONFIRMED);

        log.info("Found {} pending confirmed bookings", bookings.size());
        return bookings.stream()
                .map(this::mapToBookingResponse)
                .toList();
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
