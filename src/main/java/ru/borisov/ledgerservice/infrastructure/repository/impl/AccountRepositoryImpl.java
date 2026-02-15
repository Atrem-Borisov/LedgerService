package ru.borisov.ledgerservice.infrastructure.repository.impl;

import org.springframework.stereotype.Repository;
import ru.borisov.ledgerservice.application.account.AccountRepository;
import ru.borisov.ledgerservice.domain.account.Account;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.enums.AccountType;
import ru.borisov.ledgerservice.infrastructure.entity.AccountEntity;
import ru.borisov.ledgerservice.infrastructure.repository.AccountJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryImpl(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = new AccountEntity(
                account.id().value(),
                account.name(),
                account.type().name()
        );
        entity = jpaRepository.save(entity);
        return new Account(
                new AccountId(entity.getId()),
                entity.getName(),
                AccountType.valueOf(entity.getType())
        );
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.value())
                .map(e -> new Account(
                        new AccountId(e.getId()),
                        e.getName(),
                        AccountType.valueOf(e.getType())
                ));
    }

    @Override
    public Optional<Account> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(e -> new Account(
                        new AccountId(e.getId()),
                        e.getName(),
                        AccountType.valueOf(e.getType())
                ));
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> new Account(
                        new AccountId(e.getId()),
                        e.getName(),
                        AccountType.valueOf(e.getType())
                ))
                .toList();
    }
}
