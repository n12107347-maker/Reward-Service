package com.nag.exception;

/**
 * Thrown when no transactions are found for a customer.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * @param customerId the ID that had no transactions
     */
    public CustomerNotFoundException(String customerId) {
        super("No transactions found for customer: " + customerId);
    }
}
