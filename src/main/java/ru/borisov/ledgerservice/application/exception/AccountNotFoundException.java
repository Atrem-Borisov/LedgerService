package ru.borisov.ledgerservice.application.exception;

import ru.borisov.ledgerservice.domain.account.AccountId;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(AccountId id) {
        super("Account not found: " + id.value());
    }
}
