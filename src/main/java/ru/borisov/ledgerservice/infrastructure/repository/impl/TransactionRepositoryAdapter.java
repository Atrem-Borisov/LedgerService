package ru.borisov.ledgerservice.infrastructure.repository.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.borisov.ledgerservice.application.transaction.TransactionRepository;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.transaction.Transaction;
import ru.borisov.ledgerservice.domain.transaction.TransactionId;
import ru.borisov.ledgerservice.infrastructure.entity.AccountEntity;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntity;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntryEntity;
import ru.borisov.ledgerservice.infrastructure.mapper.TransactionMapper;
import ru.borisov.ledgerservice.infrastructure.repository.AccountJpaRepository;
import ru.borisov.ledgerservice.infrastructure.repository.TransactionJpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final AccountJpaRepository accountJpaRepository;

    public TransactionRepositoryAdapter(TransactionJpaRepository transactionJpaRepository,
                                        AccountJpaRepository accountJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {

        List<UUID> accountIds = transaction.entries().stream()
                .map(e -> e.accountId().value())
                .distinct()
                .collect(Collectors.toList());

        List<AccountEntity> accounts = accountJpaRepository.findAllById(accountIds);

        TransactionEntity txEntity = TransactionMapper.toEntity(transaction, accounts);

        TransactionEntity saved = transactionJpaRepository.save(txEntity);

        return TransactionMapper.toDomain(saved, saved.getEntries());
    }

    @Override
    public java.util.Optional<Transaction> findById(TransactionId id) {
        return transactionJpaRepository.findById(id.value())
                .map(tx -> TransactionMapper.toDomain(tx, tx.getEntries()));
    }

    @Override
    public List<Transaction> findByAccountId(AccountId accountId) {
        List<TransactionEntryEntity> entries = transactionJpaRepository.findEntriesByAccountId(accountId.value());

        return entries.stream()
                .collect(Collectors.groupingBy(TransactionEntryEntity::getTransaction))
                .entrySet()
                .stream()
                .map(e -> TransactionMapper.toDomain(e.getKey(), e.getValue()))
                .toList();
    }
}