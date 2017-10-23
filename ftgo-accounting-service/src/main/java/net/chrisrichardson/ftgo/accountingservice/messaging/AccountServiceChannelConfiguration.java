package net.chrisrichardson.ftgo.accountingservice.messaging;

public class AccountServiceChannelConfiguration {
  private String commandDispatcherId;
  private String commandChannel;

  public AccountServiceChannelConfiguration(String commandDispatcherId, String commandChannel) {
    this.commandDispatcherId = commandDispatcherId;
    this.commandChannel = commandChannel;
  }

  public String getCommandDispatcherId() {
    return commandDispatcherId;
  }

  public String getCommandChannel() {
    return commandChannel;
  }
}
