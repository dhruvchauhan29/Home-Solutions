package com.homesolutions.service.interfaces;

import com.homesolutions.dto.QuoteRequest;
import com.homesolutions.dto.QuoteResponse;

public interface PricingService {
    QuoteResponse calculateQuote(QuoteRequest request);
}
