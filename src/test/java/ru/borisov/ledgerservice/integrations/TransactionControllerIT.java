package ru.borisov.ledgerservice.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.borisov.ledgerservice.application.account.AccountRepository;
import ru.borisov.ledgerservice.application.account.AccountWithBalance;
import ru.borisov.ledgerservice.application.dto.CreateTransactionRequest;
import ru.borisov.ledgerservice.application.dto.TransactionEntryDTO;
import ru.borisov.ledgerservice.application.dto.TransactionResponse;
import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.enums.AccountType;
import ru.borisov.ledgerservice.domain.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("TransactionController Integration Tests")
class TransactionControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("ledger_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    private UUID accountId;

    @BeforeEach
    void setup() {
        AccountId id = new AccountId(UUID.randomUUID());

        Account account = new Account(
                id,
                "Test Account " + UUID.randomUUID(),
                AccountType.ASSET
        );

        accountRepository.save(account);
        accountId = id.value();
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void createTransaction_shouldReturnCreated() {

        CreateTransactionRequest request = validRequest(accountId);

        ResponseEntity<TransactionResponse> response =
                restTemplate.postForEntity(
                        "/api/transactions",
                        request,
                        TransactionResponse.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().entries()).hasSize(2);
    }

    @Test
    @DisplayName("Should return 4xx when creating transaction with non-existing account")
    void createTransaction_shouldFail_whenAccountNotExists() {

        CreateTransactionRequest request = validRequest(UUID.randomUUID());

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "/api/transactions",
                        request,
                        String.class
                );

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("Should return transaction by id")
    void getTransaction_shouldReturnTransaction() {

        TransactionResponse created = restTemplate.postForEntity(
                "/api/transactions",
                validRequest(accountId),
                TransactionResponse.class
        ).getBody();

        ResponseEntity<TransactionResponse> response =
                restTemplate.getForEntity(
                        "/api/transactions/" + created.id(),
                        TransactionResponse.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    @DisplayName("Should return list of transactions for account")
    void getTransactionsForAccount_shouldReturnList() {

        restTemplate.postForEntity(
                "/api/transactions",
                validRequest(accountId),
                TransactionResponse.class
        );

        ResponseEntity<TransactionResponse[]> response =
                restTemplate.getForEntity(
                        "/api/transactions/account/" + accountId,
                        TransactionResponse[].class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Should return empty list when account has no transactions")
    void getTransactionsForAccount_shouldReturnEmptyList_whenNone() {

        ResponseEntity<TransactionResponse[]> response =
                restTemplate.getForEntity(
                        "/api/transactions/account/" + accountId,
                        TransactionResponse[].class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    private CreateTransactionRequest validRequest(UUID accId) {
        return new CreateTransactionRequest(
                "Purchase office supplies",
                LocalDateTime.of(2026, 2, 15, 10, 30),
                List.of(
                        new TransactionEntryDTO(accId, EntryType.DEBIT, BigDecimal.valueOf(100)),
                        new TransactionEntryDTO(accId, EntryType.CREDIT, BigDecimal.valueOf(100))
                )
        );
    }

    @Test
    @DisplayName("Should reject transaction if debits don't equal credits")
    void createTransaction_shouldFail_whenDebitsNotEqualCredits() {

        CreateTransactionRequest request = new CreateTransactionRequest(
                "Unbalanced transaction",
                LocalDateTime.now(),
                List.of(
                        new TransactionEntryDTO(accountId, EntryType.DEBIT, BigDecimal.valueOf(100)),
                        new TransactionEntryDTO(accountId, EntryType.CREDIT, BigDecimal.valueOf(50))
                )
        );

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/transactions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("UnbalancedTransactionException");
    }

    @Test
    @DisplayName("Should calculate account balance correctly after multiple transactions")
    void accountBalance_shouldBeCalculatedCorrectly() {
        restTemplate.postForEntity(
                "/api/transactions",
                new CreateTransactionRequest(
                        "Transaction 1",
                        LocalDateTime.now(),
                        List.of(
                                new TransactionEntryDTO(accountId, EntryType.DEBIT, BigDecimal.valueOf(100)),
                                new TransactionEntryDTO(accountId, EntryType.CREDIT, BigDecimal.valueOf(100))
                        )
                ),
                TransactionResponse.class
        );

        restTemplate.postForEntity(
                "/api/transactions",
                new CreateTransactionRequest(
                        "Transaction 2",
                        LocalDateTime.now(),
                        List.of(
                                new TransactionEntryDTO(accountId, EntryType.DEBIT, BigDecimal.valueOf(50)),
                                new TransactionEntryDTO(accountId, EntryType.CREDIT, BigDecimal.valueOf(50))
                        )
                ),
                TransactionResponse.class
        );

        ResponseEntity<AccountWithBalance> response =
                restTemplate.getForEntity("/api/accounts/" + accountId, AccountWithBalance.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BigDecimal expectedBalance = BigDecimal.ZERO;
        assertThat(response.getBody().balance()).isEqualByComparingTo(expectedBalance);
    }


}



