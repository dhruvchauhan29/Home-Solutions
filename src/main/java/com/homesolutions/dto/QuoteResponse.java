package com.homesolutions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private BigDecimal basePrice;
    private BigDecimal extraCharge;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String details;
}
