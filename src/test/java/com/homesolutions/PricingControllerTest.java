package com.homesolutions;

import com.homesolutions.controller.PricingController;
import com.homesolutions.dto.QuoteResponse;
import com.homesolutions.service.interfaces.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricingController.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PricingService pricingService;

    @MockBean
    private com.homesolutions.security.JwtUtil jwtUtil;

    @MockBean
    private com.homesolutions.security.CustomUserDetailsService customUserDetailsService;

    private QuoteResponse mockQuote;

    @BeforeEach
    void setUp() {
        mockQuote = QuoteResponse.builder()
                .basePrice(BigDecimal.valueOf(150.00))
                .extraCharge(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .totalPrice(BigDecimal.valueOf(150.00))
                .details("Pipe Repair service - 120 minutes")
                .build();
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void testGetQuote() throws Exception {
        when(pricingService.calculateQuote(any())).thenReturn(mockQuote);

        mockMvc.perform(get("/pricing/quote")
                        .param("serviceId", "1")
                        .param("durationMinutes", "120"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(150.00))
                .andExpect(jsonPath("$.totalPrice").value(150.00))
                .andExpect(jsonPath("$.details").value("Pipe Repair service - 120 minutes"));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void testGetQuote_WithDifferentDuration() throws Exception {
        QuoteResponse quote = QuoteResponse.builder()
                .basePrice(BigDecimal.valueOf(150.00))
                .extraCharge(BigDecimal.valueOf(90.00))
                .discount(BigDecimal.ZERO)
                .totalPrice(BigDecimal.valueOf(240.00))
                .details("Pipe Repair service - 180 minutes")
                .build();

        when(pricingService.calculateQuote(any())).thenReturn(quote);

        mockMvc.perform(get("/pricing/quote")
                        .param("serviceId", "1")
                        .param("durationMinutes", "180"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(240.00))
                .andExpect(jsonPath("$.extraCharge").value(90.00));
    }

    @Test
    void testGetQuote_Unauthorized() throws Exception {
        mockMvc.perform(get("/pricing/quote")
                        .param("serviceId", "1")
                        .param("durationMinutes", "120"))
                .andExpect(status().isUnauthorized());
    }
}
