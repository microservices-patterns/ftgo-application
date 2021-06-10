package net.chrisrichardson.ftgo.accountingservice.web;

import net.chrisrichardson.ftgo.common.Money;

public class GetAccountResponse {
  private String accountId;
  private Money balance;

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public Money getBalance() {
    return balance;
  }

  public void setBalance(Money balance) {
    this.balance = balance;
  }

  public GetAccountResponse() {

  }

  public GetAccountResponse(String accountId, Money balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
}
