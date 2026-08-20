package com.nag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds the reward points earned in a single month.
 */

@Getter
@AllArgsConstructor
public class MonthlyReward {
    private String month;
    private int points;
}
