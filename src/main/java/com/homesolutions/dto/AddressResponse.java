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
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String landmark;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
