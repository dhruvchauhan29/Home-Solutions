package com.homesolutions.controller;

import com.homesolutions.dto.QuoteRequest;
import com.homesolutions.dto.QuoteResponse;
import com.homesolutions.service.interfaces.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pricing", description = "Pricing and quote endpoints")
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/quote")
    @Operation(summary = "Get price quote", description = "Calculate price quote for a service")
    public ResponseEntity<QuoteResponse> getQuote(
            @Parameter(description = "Service ID", required = true)
            @RequestParam @NotNull Long serviceId,
            @Parameter(description = "Duration in minutes", required = true)
            @RequestParam @NotNull @Positive Integer durationMinutes) {
        
        log.info("Get quote - serviceId: {}, durationMinutes: {}", serviceId, durationMinutes);
        
        QuoteRequest request = QuoteRequest.builder()
                .serviceId(serviceId)
                .durationMinutes(durationMinutes)
                .build();
        
        QuoteResponse quote = pricingService.calculateQuote(request);
        return ResponseEntity.ok(quote);
    }
}
