package ru.borisov.ledgerservice.application.account;

import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByName(String name);

    List<Account> findAll();
}
