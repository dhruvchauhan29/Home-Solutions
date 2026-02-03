package com.homesolutions.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
}
