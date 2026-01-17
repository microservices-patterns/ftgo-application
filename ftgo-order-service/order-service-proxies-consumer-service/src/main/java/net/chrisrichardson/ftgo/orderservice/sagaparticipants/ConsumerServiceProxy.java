package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceChannels;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;

@SagaParticipantProxy(channel = ConsumerServiceChannels.consumerServiceChannel)
public class ConsumerServiceProxy {

  @SagaParticipantOperation(commandClass = ValidateOrderByConsumer.class, replyClasses = Success.class)
  public CommandWithDestination validateOrder(long consumerId, long orderId, Money orderTotal) {
    return CommandWithDestinationBuilder
            .send(new ValidateOrderByConsumer(consumerId, orderId, orderTotal))
            .to(ConsumerServiceChannels.consumerServiceChannel)
            .build();
  }
}
