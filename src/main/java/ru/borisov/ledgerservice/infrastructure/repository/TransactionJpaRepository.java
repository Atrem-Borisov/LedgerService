package ru.borisov.ledgerservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntity;
import ru.borisov.ledgerservice.infrastructure.entity.TransactionEntryEntity;

import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT e FROM TransactionEntryEntity e WHERE e.account.id = :accountId")
    List<TransactionEntryEntity> findEntriesByAccountId(@Param("accountId") UUID accountId);
}
