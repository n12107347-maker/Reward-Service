package com.nag.service;

import com.nag.entity.Transaction;
import com.nag.exception.CustomerNotFoundException;
import com.nag.exception.InvalidTransactionException;
import com.nag.repository.TransactionRepository;
import com.nag.dto.MonthlyReward;
import com.nag.dto.RewardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of RewardsService.
 *
 * Points per transaction: 1 pt/dollar between $50-$100, 2 pts/dollar above $100.
 * Months are derived from transaction dates using YearMonth — nothing hardcoded.
 */

@Service
@RequiredArgsConstructor
public class RewardsServiceImpl implements RewardsService{

    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private final TransactionRepository transactionRepository;

    /**
     * {@inheritDoc}
     *
     * @throws InvalidTransactionException if customerId is blank or any transaction has invalid data
     * @throws CustomerNotFoundException if no transactions found for the customer
     */

    @Override
    @Transactional(readOnly = true)
    public RewardsResponse calculateRewards(String customerId) {
        validateCustomerId(customerId);
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        if (transactions.isEmpty()) {
            throw new CustomerNotFoundException(customerId);
        }
        transactions.forEach(this::validateTransaction);
        Map<YearMonth, Integer> monthlyPoints = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> YearMonth.from(transaction.getTransactionDate()),
                        Collectors.summingInt(transaction -> calculatePoints(transaction.getAmount()))));
        List<MonthlyReward> monthlyRewards = monthlyPoints.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new MonthlyReward(entry.getKey().toString(), entry.getValue()))
                .toList();
        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::getPoints).sum();
        return new RewardsResponse(customerId, monthlyRewards, totalPoints);
    }

    /**
     * Calculates points for a single transaction amount.
     * Under $50 = 0 pts, $50-$100 = 1 pt/dollar over $50, above $100 = 50 + 2 pts/dollar over $100.
     * Example: $120 = 50 + (20*2) = 90 pts.
     *
     * @param amount transaction amount
     * @return reward points earned
     */

    private int calculatePoints(BigDecimal amount) {
        if (amount.compareTo(FIFTY) <= 0) {
            return 0;
        }
        if (amount.compareTo(HUNDRED) <= 0) {
            return amount.subtract(FIFTY).intValue();
        }
        return 50 + amount.subtract(HUNDRED).multiply(TWO).intValue();
    }

    /**
     * Checks that a customerId was provided.
     *
     * @param customerId the ID to check
     * @throws InvalidTransactionException if null or blank
     */

    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidTransactionException("CustomerId must not be null or blank");
        }
    }

    /**
     * Validates a transaction before points are calculated.
     *
     * @param transaction the transaction to check
     * @throws InvalidTransactionException if amount is null/negative or date is null
     */

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new InvalidTransactionException("Transaction amount must not be null");
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransactionException("Transaction amount must not be negative");
        }
        if (transaction.getTransactionDate() == null) {
            throw new InvalidTransactionException("Transaction date must not be null");
        }
    }
}
