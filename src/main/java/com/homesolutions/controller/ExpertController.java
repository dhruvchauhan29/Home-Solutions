package com.homesolutions.controller;

import com.homesolutions.dto.BookingResponse;
import com.homesolutions.dto.TicketResponse;
import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.service.interfaces.ExpertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/expert")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EXPERT')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Expert", description = "Expert-specific endpoints")
public class ExpertController {

    private final ExpertService expertService;

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/onboarding")
    @Operation(summary = "Expert onboarding", description = "Submit expert onboarding details")
    public ResponseEntity<UserProfileResponse> onboard(@RequestBody Map<String, String> request) {
        String email = getAuthenticatedEmail();
        String details = request.getOrDefault("details", "");
        log.info("Expert onboarding for email: {}", email);
        UserProfileResponse profile = expertService.onboard(email, details);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/jobs")
    @Operation(summary = "Get expert jobs", description = "Get all jobs assigned to the authenticated expert")
    public ResponseEntity<Page<BookingResponse>> getJobs(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        String email = getAuthenticatedEmail();
        log.info("Get jobs for expert: {}, page: {}, size: {}", email, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> jobs = expertService.getJobs(email, pageable);
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/jobs/{bookingId}/accept")
    @Operation(summary = "Accept job", description = "Accept an assigned job")
    public ResponseEntity<BookingResponse> acceptJob(@PathVariable Long bookingId) {
        String email = getAuthenticatedEmail();
        log.info("Expert {} accepting job {}", email, bookingId);
        BookingResponse booking = expertService.acceptJob(email, bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/jobs/{bookingId}/decline")
    @Operation(summary = "Decline job", description = "Decline an assigned job")
    public ResponseEntity<BookingResponse> declineJob(@PathVariable Long bookingId) {
        String email = getAuthenticatedEmail();
        log.info("Expert {} declining job {}", email, bookingId);
        BookingResponse booking = expertService.declineJob(email, bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/jobs/{bookingId}/arrived")
    @Operation(summary = "Mark arrived", description = "Mark that the expert has arrived at the job location")
    public ResponseEntity<BookingResponse> arrivedAtJob(@PathVariable Long bookingId) {
        String email = getAuthenticatedEmail();
        log.info("Expert {} arrived at job {}", email, bookingId);
        BookingResponse booking = expertService.arrivedAtJob(email, bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/jobs/{bookingId}/start")
    @Operation(summary = "Start job", description = "Mark that the job has started")
    public ResponseEntity<BookingResponse> startJob(@PathVariable Long bookingId) {
        String email = getAuthenticatedEmail();
        log.info("Expert {} starting job {}", email, bookingId);
        BookingResponse booking = expertService.startJob(email, bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/jobs/{bookingId}/complete")
    @Operation(summary = "Complete job", description = "Mark that the job has been completed")
    public ResponseEntity<BookingResponse> completeJob(@PathVariable Long bookingId) {
        String email = getAuthenticatedEmail();
        log.info("Expert {} completing job {}", email, bookingId);
        BookingResponse booking = expertService.completeJob(email, bookingId);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/jobs/{bookingId}/issue")
    @Operation(summary = "Report issue", description = "Report an issue with a job")
    public ResponseEntity<TicketResponse> reportIssue(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> request) {
        String email = getAuthenticatedEmail();
        String issue = request.getOrDefault("issue", "");
        log.info("Expert {} reporting issue for job {}", email, bookingId);
        TicketResponse ticket = expertService.reportIssue(email, bookingId, issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
