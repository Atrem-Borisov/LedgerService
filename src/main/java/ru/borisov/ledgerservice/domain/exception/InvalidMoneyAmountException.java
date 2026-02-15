package ru.borisov.ledgerservice.domain.exception;

public class InvalidMoneyAmountException extends DomainException{

    public InvalidMoneyAmountException() {
        super("Amount must be positive");
    }
}
