package ru.borisov.ledgerservice.application.account;

import ru.borisov.ledgerservice.domain.account.Account;

import java.math.BigDecimal;
import java.util.Objects;

public record AccountWithBalance(Account account, BigDecimal balance) {

    public AccountWithBalance(Account account, BigDecimal balance) {
        this.account = Objects.requireNonNull(account);
        this.balance = Objects.requireNonNull(balance);
    }
}
