package ru.borisov.ledgerservice.infrastructure.mapper;

import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.enums.EntryType;
import ru.borisov.ledgerservice.domain.shared.Money;
import ru.borisov.ledgerservice.domain.transaction.Transaction;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntryId;
import ru.borisov.ledgerservice.domain.transaction.TransactionId;
import ru.borisov.ledgerservice.infrastructure.entity.AccountEntity;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntity;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntryEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {

    /**
     * Преобразование доменной сущности в JPA Entity
     */
    public static TransactionEntity toEntity(Transaction transaction,
                                             List<AccountEntity> accounts) {

        TransactionEntity txEntity = new TransactionEntity(
                transaction.id().value(),
                transaction.date(),
                transaction.description()
        );

        List<TransactionEntryEntity> entryEntities = transaction.entries().stream()
                .map(entry -> {
                    AccountEntity accountEntity = accounts.stream()
                            .filter(a -> a.getId().equals(entry.accountId().value()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Account not found: " + entry.accountId().value()
                            ));

                    return new TransactionEntryEntity(
                            entry.id().value(),
                            txEntity,
                            accountEntity,
                            entry.type().name(),
                            entry.amount().value()
                    );
                })
                .collect(Collectors.toList());
        txEntity.setEntries(entryEntities);

        return txEntity;
    }

    /**
     * Преобразование JPA Entity в доменную сущность
     */
    public static Transaction toDomain(TransactionEntity entity, List<TransactionEntryEntity> entries) {
        List<TransactionEntry> domainEntries = entity.getEntries().stream()
                .map(e -> new TransactionEntry(
                        new TransactionEntryId(e.getId()),
                        new AccountId(e.getAccount().getId()),
                        EntryType.valueOf(e.getEntryType()),
                        Money.of(e.getAmount())
                ))
                .collect(Collectors.toList());

        return new Transaction(
                new TransactionId(entity.getId()),
                entity.getTransactionDate(),
                entity.getDescription(),
                domainEntries
        );
    }
}
