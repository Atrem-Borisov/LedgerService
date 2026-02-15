package ru.borisov.ledgerservice.domain.account;

import ru.borisov.ledgerservice.domain.exception.InvalidAccountNameException;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record Account(AccountId id, String name, AccountType type) {

    public Account(AccountId id, String name, AccountType type) {

        if (name == null || name.isBlank()) {
            throw new InvalidAccountNameException();
        }

        this.id = Objects.requireNonNull(id);
        this.name = name.trim();
        this.type = Objects.requireNonNull(type);
    }

    public BigDecimal calculateBalance(List<TransactionEntry> entries) {

        BigDecimal balance = BigDecimal.ZERO;

        for (TransactionEntry entry : entries) {
            balance = type.apply(balance, entry.type(), entry.amount());
        }

        return balance;
    }
}
