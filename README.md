# Ledger Service

## Краткое описание
Ledger Service — это микросервис для управления счетами и транзакциями, реализующий простую систему двойной записи (double-entry accounting). Сервис позволяет:
- Создавать счета с типами (ASSET, LIABILITY и др.)
- Создавать транзакции с несколькими дебетовыми и кредитными записями
- Получать транзакции по ID или по счету
- Рассчитывать баланс счета на основе истории транзакций

Проект построен с использованием **Spring Boot**, **Spring Data JPA**, **PostgreSQL**, интеграционные тесты реализованы с помощью **Testcontainers**.

---

## Архитектура и принципы проектирования

- **Чистая архитектура:**
    - `domain` — бизнес-логика и сущности (`Account`, `Transaction`, `TransactionEntry`), в том числе расчёт баланса и проверка корректности транзакций.
    - `application` — use case'ы (`CreateTransactionUseCase`, `CreateAccountUseCase`) для управления бизнес-операциями.
    - `infrastructure` — репозитории (`AccountRepositoryImpl`, `TransactionRepositoryImpl`) и сущности базы данных.
    - `interfaces` — REST-контроллеры (`TransactionController`, `AccountController`) и DTO.

- **Валидация и инварианты:**
    - Транзакции должны содержать минимум 2 записи.
    - Транзакции автоматически проверяются на баланс дебетов и кредитов (`UnbalancedTransactionException`).
    - Названия счетов уникальны и обязательно должны быть заполнены.

- **Обработка ошибок:**
    - `GlobalExceptionHandler` преобразует исключения домена в HTTP-ответы:
        - `AccountNotFoundException` → 404 Not Found
        - `UnbalancedTransactionException` → 400 Bad Request

- **Тестирование:**
    - Интеграционные тесты используют `TestRestTemplate` и **PostgreSQL Testcontainers**.
    - Тесты покрывают создание счетов, транзакций, сценарии валидации и получение данных.

---

## Как собрать и запустить приложение

### Требования
- Java 21+
- Gradle 8+
- Docker (для запуска Postgres в тестах)

### Сборка
```bash
./gradlew clean build
