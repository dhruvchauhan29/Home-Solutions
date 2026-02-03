package com.homesolutions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long id;
    private Long bookingId;
    private Long customerId;
    private String customerName;
    private Long expertId;
    private String expertName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
