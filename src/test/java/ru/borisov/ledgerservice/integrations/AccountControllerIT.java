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
import ru.borisov.ledgerservice.application.dto.AccountResponse;
import ru.borisov.ledgerservice.application.dto.CreateAccountRequest;
import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.enums.AccountType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("AccountController Integration Tests")
class AccountControllerIT {

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
        Account account = new Account(id, "Test Account " + UUID.randomUUID(), AccountType.ASSET);
        accountRepository.save(account);
        accountId = id.value();
    }

    @Test
    @DisplayName("Should create account successfully")
    void createAccount_shouldReturnCreated() {
        CreateAccountRequest request = new CreateAccountRequest("New Account", AccountType.ASSET);

        ResponseEntity<AccountResponse> response =
                restTemplate.postForEntity("/api/accounts", request, AccountResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("New Account");
        assertThat(response.getBody().type()).isEqualTo(AccountType.ASSET);
    }

    @Test
    @DisplayName("Should return all accounts")
    void getAllAccounts_shouldReturnList() {
        ResponseEntity<AccountResponse[]> response =
                restTemplate.getForEntity("/api/accounts", AccountResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Should return account by id")
    void getAccountById_shouldReturnAccount() {
        ResponseEntity<AccountWithBalance> response =
                restTemplate.getForEntity("/api/accounts/" + accountId, AccountWithBalance.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return 404 when account not found")
    void getAccountById_shouldReturn404_whenNotExists() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/accounts/" + UUID.randomUUID(), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}

