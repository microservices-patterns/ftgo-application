package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.Event;
import net.chrisrichardson.ftgo.common.Money;

public class AccountAuthorizedEvent implements Event {
    private Money money;

    public AccountAuthorizedEvent() {
    }

    public AccountAuthorizedEvent(Money money) {
        this.money = money;
    }

    public Money getMoney() {
        return money;
    }
}
