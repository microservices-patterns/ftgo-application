package net.chrisrichardson.ftgo.endtoendtests.dto;

import java.math.BigDecimal;

public class Money {
    private BigDecimal amount;

    public Money() {
    }

    public Money(String amount) {
        this.amount = new BigDecimal(amount);
    }

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String asString() {
        return amount.toString();
    }

    public Money multiply(int multiplier) {
        return new Money(amount.multiply(new BigDecimal(multiplier)));
    }
}
