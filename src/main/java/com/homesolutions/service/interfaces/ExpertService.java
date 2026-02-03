package com.homesolutions.service.interfaces;

import com.homesolutions.dto.BookingResponse;
import com.homesolutions.dto.TicketResponse;
import com.homesolutions.dto.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpertService {
    UserProfileResponse onboard(String phone, String details);
    Page<BookingResponse> getJobs(String phone, Pageable pageable);
    BookingResponse acceptJob(String phone, Long bookingId);
    BookingResponse declineJob(String phone, Long bookingId);
    BookingResponse arrivedAtJob(String phone, Long bookingId);
    BookingResponse startJob(String phone, Long bookingId);
    BookingResponse completeJob(String phone, Long bookingId);
    TicketResponse reportIssue(String phone, Long bookingId, String issue);
}
