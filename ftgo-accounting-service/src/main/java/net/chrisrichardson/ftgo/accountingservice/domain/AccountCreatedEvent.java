package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.Event;
import net.chrisrichardson.ftgo.common.Money;

import java.math.BigDecimal;

public class AccountCreatedEvent implements Event {
    private Money initialBalance;


    private AccountCreatedEvent() {}

    public AccountCreatedEvent(Money initialBalance){
        this.initialBalance = initialBalance;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }
}
