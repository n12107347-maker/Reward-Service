package com.nag.exception;

/**
 * Thrown when request input or transaction data fails validation.
 */
public class InvalidTransactionException extends RuntimeException {

    /**
     * @param message description of what failed validation
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}
