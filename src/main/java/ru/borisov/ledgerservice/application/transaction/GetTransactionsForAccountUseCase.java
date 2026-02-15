package ru.borisov.ledgerservice.application.transaction;

import org.springframework.stereotype.Service;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.transaction.Transaction;

import java.util.List;

@Service
public class GetTransactionsForAccountUseCase {

    private final TransactionRepository transactionRepository;

    public GetTransactionsForAccountUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> execute(AccountId accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
