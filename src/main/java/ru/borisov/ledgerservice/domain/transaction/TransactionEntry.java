package ru.borisov.ledgerservice.domain.transaction;

import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.shared.Money;
import ru.borisov.ledgerservice.domain.enums.EntryType;

import java.util.Objects;

/**
 * @param id новый
 */
public record TransactionEntry(TransactionEntryId id, AccountId accountId, EntryType type, Money amount) {

    public TransactionEntry(TransactionEntryId id, AccountId accountId, EntryType type, Money amount) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
    }

    public boolean isDebit() {
        return type == EntryType.DEBIT;
    }

    public boolean isCredit() {
        return type == EntryType.CREDIT;
    }
}

