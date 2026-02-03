package com.homesolutions.service.impl;

import com.homesolutions.dto.QuoteRequest;
import com.homesolutions.dto.QuoteResponse;
import com.homesolutions.entity.Service;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.repository.ServiceRepository;
import com.homesolutions.service.interfaces.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public QuoteResponse calculateQuote(QuoteRequest request) {
        log.info("Calculating quote for serviceId: {}, duration: {} minutes", 
                request.getServiceId(), request.getDurationMinutes());

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));

        BigDecimal basePrice = service.getBasePrice();
        BigDecimal extraCharge = BigDecimal.ZERO;

        if (request.getDurationMinutes() > 60) {
            int extraMinutes = request.getDurationMinutes() - 60;
            int extraHours = (int) Math.ceil(extraMinutes / 60.0);
            extraCharge = service.getExtraHourlyRate().multiply(BigDecimal.valueOf(extraHours));
        }

        BigDecimal totalBeforeDiscount = basePrice.add(extraCharge);
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal totalPrice = totalBeforeDiscount.subtract(discount);

        String details = String.format("Base price: ₹%.2f, Extra charge: ₹%.2f, Discount: ₹%.2f",
                basePrice, extraCharge, discount);

        log.info("Quote calculated - Total: {}", totalPrice);

        return QuoteResponse.builder()
                .basePrice(basePrice)
                .extraCharge(extraCharge)
                .discount(discount)
                .totalPrice(totalPrice)
                .details(details)
                .build();
    }
}
