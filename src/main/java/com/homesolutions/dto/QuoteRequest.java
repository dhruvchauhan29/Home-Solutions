package com.homesolutions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private Integer durationMinutes;
}
