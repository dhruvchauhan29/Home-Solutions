package com.homesolutions;

import com.homesolutions.controller.ExpertController;
import com.homesolutions.dto.BookingResponse;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.dto.AddressResponse;
import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.service.interfaces.ExpertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpertController.class)
class ExpertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpertService expertService;

    @MockBean
    private com.homesolutions.security.JwtUtil jwtUtil;

    @MockBean
    private com.homesolutions.security.CustomUserDetailsService customUserDetailsService;

    private UserProfileResponse mockExpert;
    private BookingResponse mockBooking;

    @BeforeEach
    void setUp() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_EXPERT");

        mockExpert = UserProfileResponse.builder()
                .id(2L)
                .phone("9876543210")
                .email("expert@test.com")
                .fullName("Test Expert")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        ServiceResponse serviceResponse = ServiceResponse.builder()
                .id(1L)
                .name("Pipe Repair")
                .categoryId(1L)
                .categoryName("Plumbing")
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .build();

        AddressResponse addressResponse = AddressResponse.builder()
                .id(1L)
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .isDefault(true)
                .build();

        mockBooking = BookingResponse.builder()
                .id(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .service(serviceResponse)
                .address(addressResponse)
                .expertId(2L)
                .expertName("Test Expert")
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .durationMinutes(120)
                .totalPrice(BigDecimal.valueOf(150.00))
                .status("ASSIGNED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testOnboard() throws Exception {
        when(expertService.onboard(eq("9876543210"), anyString())).thenReturn(mockExpert);

        String requestBody = """
                {
                    "details": "Expert onboarding details"
                }
                """;

        mockMvc.perform(post("/expert/onboarding")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testGetJobs() throws Exception {
        Page<BookingResponse> bookingPage = new PageImpl<>(
                Collections.singletonList(mockBooking),
                PageRequest.of(0, 10),
                1
        );

        when(expertService.getJobs(eq("9876543210"), any())).thenReturn(bookingPage);

        mockMvc.perform(get("/expert/jobs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testAcceptJob() throws Exception {
        when(expertService.acceptJob("9876543210", 1L)).thenReturn(mockBooking);

        mockMvc.perform(post("/expert/jobs/1/accept")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testDeclineJob() throws Exception {
        mockBooking.setStatus("CONFIRMED");
        when(expertService.declineJob("9876543210", 1L)).thenReturn(mockBooking);

        mockMvc.perform(post("/expert/jobs/1/decline")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testStartJob() throws Exception {
        mockBooking.setStatus("IN_PROGRESS");
        when(expertService.startJob("9876543210", 1L)).thenReturn(mockBooking);

        mockMvc.perform(post("/expert/jobs/1/start")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testCompleteJob() throws Exception {
        mockBooking.setStatus("COMPLETED");
        when(expertService.completeJob("9876543210", 1L)).thenReturn(mockBooking);

        mockMvc.perform(post("/expert/jobs/1/complete")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetJobs_Unauthorized() throws Exception {
        mockMvc.perform(get("/expert/jobs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "9876543210", roles = {"EXPERT"})
    void testGetPendingBookings() throws Exception {
        java.util.List<BookingResponse> bookings = java.util.Collections.singletonList(mockBooking);
        when(expertService.getPendingConfirmedBookingsForExpert()).thenReturn(bookings);

        mockMvc.perform(get("/expert/bookings/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("ASSIGNED"));
    }

    @Test
    void testGetPendingBookings_Unauthorized() throws Exception {
        mockMvc.perform(get("/expert/bookings/pending"))
                .andExpect(status().isUnauthorized());
    }
}
