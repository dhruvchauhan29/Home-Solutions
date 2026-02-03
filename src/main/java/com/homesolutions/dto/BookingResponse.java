package com.homesolutions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private ServiceResponse service;
    private AddressResponse address;
    private Long expertId;
    private String expertName;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private BigDecimal totalPrice;
    private String couponCode;
    private BigDecimal discount;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
