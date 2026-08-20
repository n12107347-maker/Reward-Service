package com.nag.service;

import com.nag.dto.RewardsResponse;

/**
 * Service interface for reward point calculations.
 */
public interface RewardsService {
    /**
     * Calculates reward points for the given customer, grouped by month.
     *
     * @param customerId the customer ID to look up
     * @return monthly breakdown and total points
     */
    RewardsResponse calculateRewards(String customerId);
}
