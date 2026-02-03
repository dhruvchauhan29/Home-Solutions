package com.homesolutions.service.interfaces;

import com.homesolutions.dto.BookingResponse;
import com.homesolutions.dto.TicketResponse;
import com.homesolutions.dto.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpertService {
    UserProfileResponse onboard(String email, String details);
    Page<BookingResponse> getJobs(String email, Pageable pageable);
    BookingResponse acceptJob(String email, Long bookingId);
    BookingResponse declineJob(String email, Long bookingId);
    BookingResponse arrivedAtJob(String email, Long bookingId);
    BookingResponse startJob(String email, Long bookingId);
    BookingResponse completeJob(String email, Long bookingId);
    TicketResponse reportIssue(String email, Long bookingId, String issue);
}
