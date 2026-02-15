package ru.borisov.ledgerservice.application.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String name) {
        super("Account with name '" + name + "' already exists");
    }
}
