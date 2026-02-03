package com.homesolutions;

import com.homesolutions.controller.ServiceController;
import com.homesolutions.dto.ServiceResponse;
import com.homesolutions.security.CustomUserDetailsService;
import com.homesolutions.security.JwtUtil;
import com.homesolutions.service.interfaces.ServiceManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceManagementService serviceManagementService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private ServiceResponse serviceResponse1;
    private ServiceResponse serviceResponse2;
    private Page<ServiceResponse> servicePage;

    @BeforeEach
    void setUp() {
        serviceResponse1 = ServiceResponse.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Professional pipe repair service")
                .categoryId(1L)
                .categoryName("Plumbing")
                .basePrice(BigDecimal.valueOf(500.00))
                .extraHourlyRate(BigDecimal.valueOf(100.00))
                .active(true)
                .build();

        serviceResponse2 = ServiceResponse.builder()
                .id(2L)
                .name("AC Repair")
                .description("Air conditioning repair service")
                .categoryId(2L)
                .categoryName("Electrical")
                .basePrice(BigDecimal.valueOf(800.00))
                .extraHourlyRate(BigDecimal.valueOf(150.00))
                .active(true)
                .build();

        List<ServiceResponse> services = Arrays.asList(serviceResponse1, serviceResponse2);
        servicePage = new PageImpl<>(services, PageRequest.of(0, 10), services.size());
    }

    @Test
    void testGetAllServices_Success() throws Exception {
        when(serviceManagementService.getAllServices(any())).thenReturn(servicePage);

        mockMvc.perform(get("/services")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Pipe Repair"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("AC Repair"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetServiceById_Success() throws Exception {
        when(serviceManagementService.getServiceById(1L)).thenReturn(serviceResponse1);

        mockMvc.perform(get("/services/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pipe Repair"))
                .andExpect(jsonPath("$.description").value("Professional pipe repair service"))
                .andExpect(jsonPath("$.categoryName").value("Plumbing"))
                .andExpect(jsonPath("$.basePrice").value(500.00));
    }

    @Test
    void testSearchServices_Success() throws Exception {
        Page<ServiceResponse> searchPage = new PageImpl<>(
                Arrays.asList(serviceResponse1), 
                PageRequest.of(0, 10), 
                1
        );

        when(serviceManagementService.searchServices(eq(1L), eq("Pipe"), any())).thenReturn(searchPage);

        mockMvc.perform(get("/services")
                        .param("categoryId", "1")
                        .param("search", "Pipe")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Pipe Repair"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
