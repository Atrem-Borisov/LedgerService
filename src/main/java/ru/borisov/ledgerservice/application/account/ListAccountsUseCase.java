package ru.borisov.ledgerservice.application.account;

import org.springframework.stereotype.Service;
import ru.borisov.ledgerservice.domain.account.Account;

import java.util.List;

@Service
public class ListAccountsUseCase {

    private final AccountRepository accountRepository;

    public ListAccountsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> execute() {
        return accountRepository.findAll();
    }
}
