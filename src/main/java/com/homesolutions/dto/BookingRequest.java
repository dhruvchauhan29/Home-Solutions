package com.homesolutions.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotNull(message = "Scheduled date and time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private Integer durationMinutes;

    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    private String couponCode;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
