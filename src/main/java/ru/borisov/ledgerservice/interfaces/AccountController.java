package ru.borisov.ledgerservice.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borisov.ledgerservice.application.account.AccountWithBalance;
import ru.borisov.ledgerservice.application.account.CreateAccountUseCase;
import ru.borisov.ledgerservice.application.account.GetAccountUseCase;
import ru.borisov.ledgerservice.application.account.ListAccountsUseCase;
import ru.borisov.ledgerservice.application.dto.AccountResponse;
import ru.borisov.ledgerservice.application.dto.CreateAccountRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase,
                             GetAccountUseCase getAccountUseCase,
                             ListAccountsUseCase listAccountsUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.listAccountsUseCase = listAccountsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        var account = createAccountUseCase.execute(request.name(), request.type());
        return new AccountResponse(account.id().value(), account.name(), account.type());
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return listAccountsUseCase.execute().stream()
                .map(a -> new AccountResponse(a.id().value(), a.name(), a.type()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccountWithBalance getAccountById(@PathVariable UUID id) {
        return getAccountUseCase.execute(new ru.borisov.ledgerservice.domain.account.AccountId(id));
    }
}
