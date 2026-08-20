package com.nag.service;

import com.nag.dto.RewardsResponse;
import com.nag.entity.Transaction;
import com.nag.exception.CustomerNotFoundException;
import com.nag.exception.InvalidTransactionException;
import com.nag.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsServiceImpl rewardsService;

    @Test
    void shouldCalculateRewardsForMultipleTransactions() {
        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .customerId("CUST001")
                        .amount(BigDecimal.valueOf(120))
                        .transactionDate(LocalDate.of(2026, 6, 10))
                        .build(),
                Transaction.builder()
                        .customerId("CUST001")
                        .amount(BigDecimal.valueOf(75))
                        .transactionDate(LocalDate.of(2026, 6, 15))
                        .build(),
                Transaction.builder()
                        .customerId("CUST001")
                        .amount(BigDecimal.valueOf(150))
                        .transactionDate(LocalDate.of(2026, 7, 5))
                        .build());

        when(transactionRepository.findByCustomerId("CUST001")).thenReturn(transactions);

        RewardsResponse response = rewardsService.calculateRewards("CUST001");

        assertEquals("CUST001", response.getCustomerId());
        assertEquals(2, response.getMonthlyRewards().size());
        assertEquals(115, response.getMonthlyRewards().get(0).getPoints()); // June = 90 + 25
        assertEquals(150, response.getMonthlyRewards().get(1).getPoints()); // July = 150
        assertEquals(265, response.getTotalPoints());
    }

    @ParameterizedTest(name = "amount={0} -> expectedPoints={1}")
    @CsvSource({"120, 90", "50, 0", "100, 50", "150, 150", "40, 0"})
    void shouldCalculatePointsAcrossBoundaries(int amount, int expectedPoints) {
        Transaction transaction = Transaction.builder()
                .customerId("CUST001")
                .amount(BigDecimal.valueOf(amount))
                .transactionDate(LocalDate.of(2026, 6, 10))
                .build();

        when(transactionRepository.findByCustomerId("CUST001")).thenReturn(List.of(transaction));

        RewardsResponse response = rewardsService.calculateRewards("CUST001");

        assertEquals(expectedPoints, response.getMonthlyRewards().get(0).getPoints());
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {
        when(transactionRepository.findByCustomerId("UNKNOWN")).thenReturn(List.of());

        assertThrows(CustomerNotFoundException.class, () -> rewardsService.calculateRewards("UNKNOWN"));
    }

    @Test
    void shouldRejectNegativeTransactionAmount() {
        Transaction transaction = Transaction.builder()
                .customerId("CUST001")
                .amount(BigDecimal.valueOf(-10))
                .transactionDate(LocalDate.of(2026, 6, 10))
                .build();

        when(transactionRepository.findByCustomerId("CUST001")).thenReturn(List.of(transaction));

        assertThrows(InvalidTransactionException.class, () -> rewardsService.calculateRewards("CUST001"));
    }

    @Test
    void shouldRejectNullTransactionAmount() {
        Transaction transaction = Transaction.builder()
                .customerId("CUST001")
                .amount(null)
                .transactionDate(LocalDate.of(2026, 6, 10))
                .build();

        when(transactionRepository.findByCustomerId("CUST001")).thenReturn(List.of(transaction));

        assertThrows(InvalidTransactionException.class, () -> rewardsService.calculateRewards("CUST001"));
    }

    @Test
    void shouldRejectNullTransactionDate() {
        Transaction transaction = Transaction.builder()
                .customerId("CUST001")
                .amount(BigDecimal.valueOf(100))
                .transactionDate(null)
                .build();

        when(transactionRepository.findByCustomerId("CUST001")).thenReturn(List.of(transaction));

        assertThrows(InvalidTransactionException.class, () -> rewardsService.calculateRewards("CUST001"));
    }

    @Test
    void shouldRejectBlankCustomerId() {
        assertThrows(InvalidTransactionException.class, () -> rewardsService.calculateRewards(""));
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThrows(InvalidTransactionException.class, () -> rewardsService.calculateRewards(null));
    }
}