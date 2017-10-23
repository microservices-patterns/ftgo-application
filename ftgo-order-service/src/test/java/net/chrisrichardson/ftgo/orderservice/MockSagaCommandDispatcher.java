package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MockSagaCommandDispatcher extends SagaCommandDispatcher {

  public MockSagaCommandDispatcher(String commandDispatcherId, CommandHandlers target) {
    super(commandDispatcherId, target);
  }





}
