package com.homesolutions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    private Long bookingId;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
}
