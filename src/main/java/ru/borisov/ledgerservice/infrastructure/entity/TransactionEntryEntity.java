package ru.borisov.ledgerservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction_entries",
        indexes = {
                @Index(name = "idx_transaction_entries_account_id", columnList = "account_id"),
                @Index(name = "idx_transaction_entries_transaction_id", columnList = "transaction_id")
        })
@Getter
@Setter
public class TransactionEntryEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "entry_type", nullable = false)
    private String entryType;

    @Column(nullable = false)
    private BigDecimal amount;

    public TransactionEntryEntity() {}

    public TransactionEntryEntity(UUID id, TransactionEntity transaction, AccountEntity account, String entryType, BigDecimal amount) {
        this.id = id;
        this.transaction = transaction;
        this.account = account;
        this.entryType = entryType;
        this.amount = amount;
    }
}
