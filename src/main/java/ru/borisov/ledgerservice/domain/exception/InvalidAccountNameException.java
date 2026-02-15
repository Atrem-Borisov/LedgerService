package ru.borisov.ledgerservice.domain.exception;

public class InvalidAccountNameException extends DomainException {

    public InvalidAccountNameException() {
        super("Account name cannot be empty");
    }
}
