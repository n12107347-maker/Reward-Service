package com.nag.repository;

import com.nag.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for Transaction entities.
 */

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    /**
     * Returns all transactions for the given customer, empty list if none found.
     *
     * @param customerId the customer to look up
     * @return list of transactions
     */

    List<Transaction> findByCustomerId(String customerId);
}
