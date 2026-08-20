-- Schema for the Customer Rewards Points service.
-- Executed automatically on startup by Spring Boot's SQL initializer (H2 in-memory database).

DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
                              id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                              customer_id       VARCHAR(50) NOT NULL,
                              amount            DECIMAL(12, 2) NOT NULL,
                              transaction_date  DATE NOT NULL);

CREATE INDEX idx_transactions_customer_id ON transactions (customer_id);