-- Demo seed data for the Customer Rewards Points service.
-- Three customers, each with transactions spread across three distinct
-- calendar months (June/July/August 2026), covering every points-formula
-- edge case: below $50, exactly $50, exactly $100, the $120 example from

-- CUST001: June = 120(90) + 75(25) = 115 | July = 150(150) | August = 150(150)
-- Total = 415 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST001', 120.00, '2026-06-10');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST001', 75.00,  '2026-06-15');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST001', 150.00, '2026-07-05');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST001', 150.00, '2026-08-12');

-- CUST002: June = 60(10) | July = 200(250) | August = 90(40)
-- Total = 300 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST002', 60.00,  '2026-06-01');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST002', 200.00, '2026-07-05');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST002', 90.00,  '2026-08-20');

-- CUST003: edge cases only. June = 50.00 (0, boundary) | July = 100.00 (50, boundary) | August = 30.00 (0)
-- Total = 50 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST003', 50.00,  '2026-06-05');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST003', 100.00, '2026-07-10');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES ('CUST003', 30.00,  '2026-08-15');