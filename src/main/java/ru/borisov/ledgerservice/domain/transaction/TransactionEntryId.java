package ru.borisov.ledgerservice.domain.transaction;

import java.util.UUID;

public record TransactionEntryId(UUID value) {

    public static TransactionEntryId newId() {
        return new TransactionEntryId(UUID.randomUUID());
    }
}
