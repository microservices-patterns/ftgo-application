package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.Event;
import net.chrisrichardson.ftgo.common.Money;

import java.math.BigDecimal;

public class AccountCreatedEvent implements Event {
    private Money initialBalance;

    private Long customerId;

    private AccountCreatedEvent() {}

    public AccountCreatedEvent(Long customerId, Money initialBalance){
        this.customerId = customerId;
        this.initialBalance = initialBalance;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
