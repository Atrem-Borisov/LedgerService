package ru.borisov.ledgerservice.domain.transaction;

import java.util.UUID;

public record TransactionId(UUID value) {

    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
    }

    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID());
    }
}
