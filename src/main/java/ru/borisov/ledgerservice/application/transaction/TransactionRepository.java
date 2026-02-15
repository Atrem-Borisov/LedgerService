package ru.borisov.ledgerservice.application.transaction;

import org.springframework.stereotype.Repository;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.transaction.Transaction;
import ru.borisov.ledgerservice.domain.transaction.TransactionId;

import java.util.List;
import java.util.Optional;
@Repository
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findByAccountId(AccountId accountId);
}
