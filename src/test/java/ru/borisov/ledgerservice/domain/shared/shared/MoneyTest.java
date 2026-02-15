package ru.borisov.ledgerservice.domain.shared.shared;

import org.junit.jupiter.api.Test;
import ru.borisov.ledgerservice.domain.exception.InvalidMoneyAmountException;
import ru.borisov.ledgerservice.domain.shared.Money;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class MoneyTest {

    @Test
    void should_create_money_when_amount_is_positive() {
        Money money = Money.of(new BigDecimal("100.00"));

        assertThat(money.value()).isEqualByComparingTo("100.00");
    }

    @Test
    void should_throw_exception_when_amount_is_zero() {
        assertThatThrownBy(() -> Money.of(BigDecimal.ZERO))
                .isInstanceOf(InvalidMoneyAmountException.class);
    }

    @Test
    void should_throw_exception_when_amount_is_negative() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-10")))
                .isInstanceOf(InvalidMoneyAmountException.class);
    }
}
