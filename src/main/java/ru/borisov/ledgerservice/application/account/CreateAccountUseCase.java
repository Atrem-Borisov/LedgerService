package ru.borisov.ledgerservice.application.account;

import org.springframework.stereotype.Service;
import ru.borisov.ledgerservice.application.exception.AccountAlreadyExistsException;
import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.enums.AccountType;

import java.util.Objects;
@Service
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Account execute(String name, AccountType type) {

        accountRepository.findByName(name)
                .ifPresent(a -> {
                    throw new AccountAlreadyExistsException(name);
                });

        Account account = new Account(
                AccountId.newId(),
                name,
                type
        );

        return accountRepository.save(account);
    }
}
