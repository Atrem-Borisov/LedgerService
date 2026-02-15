package ru.borisov.ledgerservice.domain.exception;

public class InvalidTransactionException extends DomainException {

    public InvalidTransactionException(String message) {
        super(message);
    }
}
