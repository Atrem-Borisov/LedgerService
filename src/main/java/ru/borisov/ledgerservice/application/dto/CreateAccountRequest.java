package ru.borisov.ledgerservice.application.dto;

import ru.borisov.ledgerservice.domain.enums.AccountType;

public record CreateAccountRequest(
        String name,
        AccountType type
) {}
