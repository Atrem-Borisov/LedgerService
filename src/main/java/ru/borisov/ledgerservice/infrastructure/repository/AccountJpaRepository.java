package ru.borisov.ledgerservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borisov.ledgerservice.infrastructure.entity.AccountEntity;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByName(String name);
}
