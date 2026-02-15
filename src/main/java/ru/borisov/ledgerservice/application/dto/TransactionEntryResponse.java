package ru.borisov.ledgerservice.application.dto;

import ru.borisov.ledgerservice.domain.enums.EntryType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionEntryResponse(
        UUID accountId,
        EntryType type,
        BigDecimal amount
) {}
