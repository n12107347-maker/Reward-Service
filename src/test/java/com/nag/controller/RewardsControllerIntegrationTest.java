package com.nag.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RewardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCustomerRewards() throws Exception {
        mockMvc.perform(get("/api/v1/customers/CUST001/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST001"))
                .andExpect(jsonPath("$.monthlyRewards.length()").value(3))
                .andExpect(jsonPath("$.monthlyRewards[0].month").value("2026-06"))
                .andExpect(jsonPath("$.monthlyRewards[0].points").value(115))
                .andExpect(jsonPath("$.monthlyRewards[1].month").value("2026-07"))
                .andExpect(jsonPath("$.monthlyRewards[1].points").value(150))
                .andExpect(jsonPath("$.monthlyRewards[2].month").value("2026-08"))
                .andExpect(jsonPath("$.monthlyRewards[2].points").value(150))
                .andExpect(jsonPath("$.totalPoints").value(415));
    }

    @Test
    void shouldReturnRewardsForAnotherCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/CUST002/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST002"))
                .andExpect(jsonPath("$.monthlyRewards.length()").value(3))
                .andExpect(jsonPath("$.totalPoints").value(300));
    }

    @Test
    void shouldHandleBoundaryAmountsCorrectly() throws Exception {
        mockMvc.perform(get("/api/v1/customers/CUST003/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRewards[0].points").value(0))   // exactly $50
                .andExpect(jsonPath("$.monthlyRewards[1].points").value(50))  // exactly $100
                .andExpect(jsonPath("$.monthlyRewards[2].points").value(0))   // below $50
                .andExpect(jsonPath("$.totalPoints").value(50));
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/UNKNOWN/rewards"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No transactions found for customer: UNKNOWN"));
    }

    @Test
    void shouldReturnBadRequestForBlankCustomerId() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/rewards", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}