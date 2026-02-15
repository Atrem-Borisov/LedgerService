package ru.borisov.ledgerservice.domain.enums;

import ru.borisov.ledgerservice.domain.shared.Money;

import java.math.BigDecimal;

public enum AccountType {
    ASSET,
    LIABILITY,
    REVENUE,
    EXPENSE;

    public BigDecimal apply(BigDecimal current, EntryType entryType, Money amount) {

        boolean increase =
                switch (this) {
                    case ASSET, EXPENSE -> entryType == EntryType.DEBIT;
                    case LIABILITY, REVENUE -> entryType == EntryType.CREDIT;
                };

        return increase
                ? current.add(amount.value())
                : current.subtract(amount.value());
    }
}
