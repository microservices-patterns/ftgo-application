package net.chrisrichardson.ftgo.common;

import io.eventuate.javaclient.commonimpl.JSonMapper;

import javax.annotation.PostConstruct;

public class CommonJsonMapperInitializer {

  @PostConstruct
  public void initialize() {
    registerMoneyModule();
  }

  public static void registerMoneyModule() {
    JSonMapper.objectMapper.registerModule(new MoneyModule());
  }
}
