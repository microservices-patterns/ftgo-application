package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.common.Money;

@SagaParticipantProxy(channel = AccountingServiceChannels.accountingServiceChannel)
public class AccountingServiceProxy {

  @SagaParticipantOperation(commandClass = AuthorizeCommand.class, replyClasses = Success.class)
  public CommandWithDestination authorize(long consumerId, Long orderId, Money orderTotal) {
    return CommandWithDestinationBuilder
            .send(new AuthorizeCommand(consumerId, orderId, orderTotal))
            .to(AccountingServiceChannels.accountingServiceChannel)
            .build();
  }
}
