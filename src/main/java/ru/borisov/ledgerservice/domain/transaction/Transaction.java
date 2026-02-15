package ru.borisov.ledgerservice.domain.transaction;

import ru.borisov.ledgerservice.domain.exception.InvalidTransactionException;
import ru.borisov.ledgerservice.domain.exception.UnbalancedTransactionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record Transaction(TransactionId id, LocalDateTime date, String description, List<TransactionEntry> entries) {

    public Transaction(
            TransactionId id,
            LocalDateTime date,
            String description,
            List<TransactionEntry> entries
    ) {

        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException("Description cannot be empty");
        }

        if (entries == null || entries.size() < 2) {
            throw new InvalidTransactionException(
                    "Transaction must contain at least two entries"
            );
        }

        this.id = Objects.requireNonNull(id);
        this.date = Objects.requireNonNull(date);
        this.description = description.trim();
        this.entries = List.copyOf(entries);

        validateBalanced();
    }

    private void validateBalanced() {

        BigDecimal totalDebits = entries.stream()
                .filter(TransactionEntry::isDebit)
                .map(e -> e.amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = entries.stream()
                .filter(TransactionEntry::isCredit)
                .map(e -> e.amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new UnbalancedTransactionException();
        }
    }
}
