package ru.borisov.ledgerservice.domain.shared.account;

import org.junit.jupiter.api.Test;
import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.shared.Money;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.enums.AccountType;
import ru.borisov.ledgerservice.domain.enums.EntryType;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntryId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountTest {

    @Test
    void should_calculate_balance_for_asset_account() {

        Account account = new Account(
                new AccountId(UUID.randomUUID()),
                "Cash",
                AccountType.ASSET
        );

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account.id(),
                EntryType.DEBIT,
                Money.of(new BigDecimal("200"))
        );

        TransactionEntry credit = new TransactionEntry(
                TransactionEntryId.newId(),
                account.id(),
                EntryType.CREDIT,
                Money.of(new BigDecimal("50"))
        );

        BigDecimal balance = account.calculateBalance(List.of(debit, credit));

        assertThat(balance).isEqualByComparingTo("150");
    }

    @Test
    void should_calculate_balance_for_liability_account() {

        Account account = new Account(
                new AccountId(UUID.randomUUID()),
                "Loan",
                AccountType.LIABILITY
        );

        TransactionEntry credit = new TransactionEntry(
                TransactionEntryId.newId(),
                account.id(),
                EntryType.CREDIT,
                Money.of(new BigDecimal("300"))
        );

        TransactionEntry debit = new TransactionEntry(
                TransactionEntryId.newId(),
                account.id(),
                EntryType.DEBIT,
                Money.of(new BigDecimal("100"))
        );

        BigDecimal balance = account.calculateBalance(List.of(credit, debit));

        assertThat(balance).isEqualByComparingTo("200");
    }
}
