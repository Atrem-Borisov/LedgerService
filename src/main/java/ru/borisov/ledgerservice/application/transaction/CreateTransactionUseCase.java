package ru.borisov.ledgerservice.application.transaction;

import org.springframework.stereotype.Service;
import ru.borisov.ledgerservice.application.account.AccountRepository;
import ru.borisov.ledgerservice.application.exception.AccountNotFoundException;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.transaction.Transaction;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.transaction.TransactionId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
@Service
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public CreateTransactionUseCase(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository
    ) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Transaction execute(
            String description,
            LocalDateTime date,
            List<TransactionEntry> entries
    ) {

        for (TransactionEntry entry : entries) {
            AccountId accountId = entry.accountId();

            accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
        }

        Transaction transaction = new Transaction(
                TransactionId.newId(),
                date,
                description,
                entries
        );
        return transactionRepository.save(transaction);
    }
}
