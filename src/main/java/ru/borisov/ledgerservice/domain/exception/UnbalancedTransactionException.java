package ru.borisov.ledgerservice.domain.exception;

public class UnbalancedTransactionException extends DomainException{

    public UnbalancedTransactionException() {
        super("Total debits must equal total credits");
    }
}
