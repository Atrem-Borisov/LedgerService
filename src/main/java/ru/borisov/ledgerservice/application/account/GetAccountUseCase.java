package ru.borisov.ledgerservice.application.account;

import org.springframework.stereotype.Service;
import ru.borisov.ledgerservice.application.exception.AccountNotFoundException;
import ru.borisov.ledgerservice.application.transaction.TransactionRepository;
import ru.borisov.ledgerservice.domain.account.AccountId;
@Service
public class GetAccountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public GetAccountUseCase(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public AccountWithBalance execute(AccountId id) {

        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        var transactions = transactionRepository.findByAccountId(id);

        var allEntries = transactions.stream()
                .flatMap(t -> t.entries().stream())
                .filter(e -> e.accountId().equals(id))
                .toList();

        var balance = account.calculateBalance(allEntries);

        return new AccountWithBalance(account, balance);
    }
}
