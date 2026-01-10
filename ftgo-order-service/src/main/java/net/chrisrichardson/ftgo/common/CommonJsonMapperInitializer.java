package net.chrisrichardson.ftgo.common;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.eventuate.common.json.mapper.JSonMapper;

import javax.annotation.PostConstruct;

public class CommonJsonMapperInitializer {

  @PostConstruct
  public void initialize() {
    registerMoneyModule();
  }

  public static void registerMoneyModule() {
    JSonMapper.objectMapper.registerModule(new MoneyModule());
    JSonMapper.objectMapper.registerModule(new JavaTimeModule());
    JSonMapper.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  }
}
