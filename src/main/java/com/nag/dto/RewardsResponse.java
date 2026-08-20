package com.nag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * API response containing a customer's reward points per month and overall total.
 */

@Getter
@AllArgsConstructor
public class RewardsResponse {
    private String customerId;
    private List<MonthlyReward> monthlyRewards;
    private int totalPoints;
}
