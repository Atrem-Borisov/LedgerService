package ru.borisov.ledgerservice.domain.shared.transaction;

import org.junit.jupiter.api.Test;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.exception.InvalidTransactionException;
import ru.borisov.ledgerservice.domain.exception.UnbalancedTransactionException;
import ru.borisov.ledgerservice.domain.shared.Money;
import ru.borisov.ledgerservice.domain.transaction.Transaction;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntryId;
import ru.borisov.ledgerservice.domain.transaction.TransactionId;
import ru.borisov.ledgerservice.domain.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TransactionTest {

    private final AccountId account1 = new AccountId(UUID.randomUUID());
    private final AccountId account2 = new AccountId(UUID.randomUUID());

    @Test
    void should_create_transaction_when_balanced() {

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account1,
                EntryType.DEBIT,
                Money.of(new BigDecimal("100"))
        );

        TransactionEntry credit = new TransactionEntry(
                TransactionEntryId.newId(),
                account2,
                EntryType.CREDIT,
                Money.of(new BigDecimal("100"))
        );

        Transaction transaction = new Transaction(
                TransactionId.newId(),
                LocalDateTime.now(),
                "Office supplies",
                List.of(debit, credit)
        );

        assertThat(transaction.entries()).hasSize(2);
    }

    @Test
    void should_throw_exception_when_not_balanced() {

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account1,
                EntryType.DEBIT,
                Money.of(new BigDecimal("100"))
        );

        TransactionEntry credit = new TransactionEntry(
                TransactionEntryId.newId(),
                account2,
                EntryType.CREDIT,
                Money.of(new BigDecimal("50"))
        );

        assertThatThrownBy(() -> new Transaction(
                TransactionId.newId(),
                LocalDateTime.now(),
                "Invalid transaction",
                List.of(debit, credit)
        )).isInstanceOf(UnbalancedTransactionException.class);
    }

    @Test
    void should_throw_exception_when_less_than_two_entries() {

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account1,
                EntryType.DEBIT,
                Money.of(new BigDecimal("100"))
        );

        assertThatThrownBy(() -> new Transaction(
                TransactionId.newId(),
                LocalDateTime.now(),
                "Invalid transaction",
                List.of(debit)
        )).isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void should_throw_exception_when_description_is_empty() {

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account1,
                EntryType.DEBIT,
                Money.of(new BigDecimal("100"))
        );

        TransactionEntry credit = new TransactionEntry(
                TransactionEntryId.newId(),
                account2,
                EntryType.CREDIT,
                Money.of(new BigDecimal("100"))
        );

        assertThatThrownBy(() -> new Transaction(
                TransactionId.newId(),
                LocalDateTime.now(),
                "",
                List.of(debit, credit)
        )).isInstanceOf(InvalidTransactionException.class);
    }
}
