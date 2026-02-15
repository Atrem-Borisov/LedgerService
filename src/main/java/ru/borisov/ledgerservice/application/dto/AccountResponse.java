package ru.borisov.ledgerservice.application.dto;

import ru.borisov.ledgerservice.domain.enums.AccountType;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        AccountType type
) {}
