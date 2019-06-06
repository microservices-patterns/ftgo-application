package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;

import javax.inject.Inject;

public class SagaMessagingTestHelper {

  @Inject
  ContractVerifierMessaging contractVerifierMessaging;

  @Autowired
  private SagaCommandProducer sagaCommandProducer;

  @Autowired
  private IdGenerator idGenerator;


  public <C extends Command, R> R sendAndReceiveCommand(CommandEndpoint<C> commandEndpoint, C command, Class<R> replyClass, String sagaType) {
    // TODO verify that replyClass is allowed

    String sagaId = idGenerator.genId().asString();

    String replyTo = sagaType + "-reply";
    sagaCommandProducer.sendCommand(sagaType, sagaId, commandEndpoint.getCommandChannel(), null, command, replyTo);

    ContractVerifierMessage response = contractVerifierMessaging.receive(replyTo);

    String payload = (String) response.getPayload();
    return (R) JSonMapper.fromJson(payload, replyClass);
  }
}
