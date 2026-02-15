package ru.borisov.ledgerservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String description,
        @JsonProperty("timestamp")
        LocalDateTime date,
        List<TransactionEntryResponse> entries
) {}
