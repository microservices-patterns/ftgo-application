package net.chrisrichardson.ftgo.accountservice.api;

import io.eventuate.tram.commands.common.Command;
import net.chrisrichardson.ftgo.common.Money;

public class CheckAccountLimitCommand implements Command {

    private Money money;
    private Long consumerId;
    private Long orderId;

    private CheckAccountLimitCommand() {
    }

    public CheckAccountLimitCommand(Long consumerId, Money money, Long orderId) {
        this.money = money;
        this.consumerId = consumerId;
        this.orderId = orderId;
    }

    public Money getMoney() {
        return money;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
