package net.chrisrichardson.ftgo.accountingservice.domain;

import net.chrisrichardson.ftgo.common.Money;

import java.math.BigDecimal;

public class CreateAccountCommand implements AccountCommand {
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    private Long customerId;
    private Money initialBalance;

    private CreateAccountCommand(){}

    public CreateAccountCommand(Long customerId, Money initialBalance){
        this.customerId = customerId;
        this.initialBalance = initialBalance;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }
}
