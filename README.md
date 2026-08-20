# Rewards Service

Spring Boot REST API that calculates reward points for retail customers based on their purchase transactions.

## Business Rule

- 0 points for the first $50 of any transaction
- 1 point per dollar between $50 and $100
- 2 points per dollar above $100

Example: $120 purchase = (50 × 1) + (20 × 2) = 90 points

Points are grouped by month and totalled per customer. Months are derived dynamically from transaction dates — nothing hardcoded.

## Tech Stack

- Java 21, Spring Boot 3.2.5
- H2 in-memory database (schema.sql + data.sql)
- JUnit 5, Mockito, MockMvc
- Maven, Lombok

## Project Structure

```
src/main/java/com/nag/
├── RewardsApplication.java
├── controller/RewardsController.java
├── service/RewardsService.java
│            RewardsServiceImpl.java
├── repository/TransactionRepository.java
├── entity/Transaction.java
├── dto/MonthlyReward.java
│        RewardsResponse.java
└── exception/CustomerNotFoundException.java
              InvalidTransactionException.java
              ErrorResponse.java
              handler/GlobalExceptionHandler.java

src/main/resources/
├── schema.sql
├── data.sql
└── application.yml
```

## API

**GET** `/api/v1/customers/{customerId}/rewards`

```json
{
  "customerId": "CUST001",
  "monthlyRewards": [
    { "month": "2026-06", "points": 115 },
    { "month": "2026-07", "points": 150 },
    { "month": "2026-08", "points": 150 }
  ],
  "totalPoints": 415
}
```

| Status | When |
|--------|------|
| 404 | No transactions found for that customer ID |
| 400 | Blank/null customer ID or invalid transaction data |
| 500 | Unexpected error (never leaks a stack trace) |

## Demo Data

Three customers seeded via data.sql across June–August 2026:

| Customer | Notes | Total |
|----------|-------|-------|
| CUST001 | Includes the $120 spec example plus mixed amounts | 415 pts |
| CUST002 | Small, mid-range, and large transactions | 300 pts |
| CUST003 | Boundary only: exactly $50, $100, and below $50 | 50 pts |

## Running

```bash
mvn spring-boot:run
```

API: `http://localhost:8080/api/v1/customers/{customerId}/rewards`

H2 console: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:rewardsdb` | User: `sa` | Password: _(empty)_

## Tests

```bash
mvn test
```

- **RewardsServiceTest** — unit tests with Mockito. Covers points formula at each boundary (parameterized), multi-month grouping, and all validation/exception paths.
- **RewardsControllerIntegrationTest** — full Spring context with H2. Covers all 3 customers end-to-end, 404 for unknown customer, 400 for blank ID.

## Notes

- `BigDecimal` used throughout to avoid floating-point issues with currency values
- Fractional cents are truncated via `intValue()` — spec examples use whole-dollar amounts
- No separate Customer entity — a customer exists if they have at least one transaction on record