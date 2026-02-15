package ru.borisov.ledgerservice.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borisov.ledgerservice.application.dto.CreateTransactionRequest;
import ru.borisov.ledgerservice.application.dto.TransactionEntryResponse;
import ru.borisov.ledgerservice.application.dto.TransactionResponse;
import ru.borisov.ledgerservice.application.transaction.CreateTransactionUseCase;
import ru.borisov.ledgerservice.application.transaction.GetTransactionUseCase;
import ru.borisov.ledgerservice.application.transaction.GetTransactionsForAccountUseCase;
import ru.borisov.ledgerservice.domain.account.AccountId;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntry;
import ru.borisov.ledgerservice.domain.transaction.TransactionEntryId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final GetTransactionsForAccountUseCase getTransactionsForAccountUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 GetTransactionUseCase getTransactionUseCase,
                                 GetTransactionsForAccountUseCase getTransactionsForAccountUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
        this.getTransactionsForAccountUseCase = getTransactionsForAccountUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody CreateTransactionRequest request) {

        List<ru.borisov.ledgerservice.domain.transaction.TransactionEntry> entries =
                request.entries().stream()
                        .map(dto -> {
                            var accountId = new AccountId(dto.accountId());
                            return new TransactionEntry(
                                    TransactionEntryId.newId(),
                                    accountId,
                                    dto.type(),
                                    ru.borisov.ledgerservice.domain.shared.Money.of(dto.amount())
                            );
                        })
                        .toList();

        var transaction = createTransactionUseCase.execute(
                request.description(),
                request.date(),
                entries
        );

        return mapToResponse(transaction);
    }


    @GetMapping("/{id}")
    public TransactionResponse getTransaction(@PathVariable UUID id) {
        var transaction = getTransactionUseCase.execute(new ru.borisov.ledgerservice.domain.transaction.TransactionId(id));
        return mapToResponse(transaction);
    }

    @GetMapping("/account/{accountId}")
    public List<TransactionResponse> getTransactionsForAccount(@PathVariable UUID accountId) {
        var transactions = getTransactionsForAccountUseCase.execute(
                new AccountId(accountId)
        );

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(ru.borisov.ledgerservice.domain.transaction.Transaction tx) {
        List<TransactionEntryResponse> entries = tx.entries().stream()
                .map(e -> new TransactionEntryResponse(
                        e.accountId().value(),
                        e.type(),
                        e.amount().value()
                ))
                .toList();

        return new TransactionResponse(
                tx.id().value(),
                tx.description(),
                tx.date(),
                entries
        );
    }
}
