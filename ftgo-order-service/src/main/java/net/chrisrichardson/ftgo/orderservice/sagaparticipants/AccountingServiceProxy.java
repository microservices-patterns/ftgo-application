package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.accountservice.api.CheckAccountLimitCommand;

public class AccountingServiceProxy {

  public final CommandEndpoint<AuthorizeCommand> authorize= CommandEndpointBuilder
          .forCommand(AuthorizeCommand.class)
          .withChannel(AccountingServiceChannels.accountingServiceChannel)
          .withReply(Success.class)
          .build();

  public final CommandEndpoint<CheckAccountLimitCommand> checkAccountLimit= CommandEndpointBuilder
          .forCommand(CheckAccountLimitCommand.class)
          .withChannel(AccountingServiceChannels.accountingServiceChannel)
          .withReply(Success.class)
          .build();
}
