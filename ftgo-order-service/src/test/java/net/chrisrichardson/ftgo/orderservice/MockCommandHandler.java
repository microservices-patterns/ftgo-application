package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.consumer.CommandMessage;

import java.util.Map;

public interface MockCommandHandler {
  Object invoke(String messageType, String resource, CommandMessage commandMessage, Map<String, String> pathVariableValues);
}
