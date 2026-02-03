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
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal basePrice;
    private BigDecimal extraHourlyRate;
    private Boolean active;
    private LocalDateTime createdAt;
}
