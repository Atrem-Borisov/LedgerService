package ru.borisov.ledgerservice.domain.shared;

import ru.borisov.ledgerservice.domain.exception.InvalidMoneyAmountException;

import java.math.BigDecimal;

public final class Money {

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidMoneyAmountException();
        }

        if (amount.signum() <= 0) {
            throw new InvalidMoneyAmountException();
        }

        this.amount = amount.stripTrailingZeros();
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public BigDecimal value() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }
}
