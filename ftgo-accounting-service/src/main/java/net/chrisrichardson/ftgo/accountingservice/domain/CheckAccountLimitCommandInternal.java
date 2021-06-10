package net.chrisrichardson.ftgo.accountingservice.domain;

import io.eventuate.tram.commands.common.Command;
import net.chrisrichardson.ftgo.common.Money;

public class CheckAccountLimitCommandInternal implements AccountCommand, Command {
  private Money money;
  private String consumerId;
  private String orderId;

  private CheckAccountLimitCommandInternal() {
  }

  public CheckAccountLimitCommandInternal(String consumerId, String orderId, Money money) {
    this.money = money;
    this.consumerId = consumerId;
    this.orderId = orderId;
  }

  public Money getMoney() {
    return money;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public String getOrderId() {
    return orderId;
  }
}
