package net.chrisrichardson.ftgo.orderservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan
public class OrderWebConfiguration {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return JSonMapper.objectMapper;
  }

}
