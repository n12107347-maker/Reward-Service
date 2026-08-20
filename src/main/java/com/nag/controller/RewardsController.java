package com.nag.controller;

import com.nag.dto.RewardsResponse;
import com.nag.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the rewards API. Handles customer reward point lookups.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class RewardsController {

    private final RewardsService rewardsService;

    /**
     * Returns reward points for a customer broken down by month with a total.
     *
     * @param customerId the customer ID (e.g. CUST001)
     * @return 200 with the rewards summary
     */
    @GetMapping("/{customerId}/rewards")
    public ResponseEntity<RewardsResponse> getRewards(@PathVariable String customerId) {
        return ResponseEntity.ok(rewardsService.calculateRewards(customerId));
    }
}