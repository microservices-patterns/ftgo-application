package net.chrisrichardson.ftgo.orderservice.web;

import brave.sampler.Sampler;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.orderservice.domain.OrderServiceWithRepositoriesConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan
@Import(OrderServiceWithRepositoriesConfiguration.class)
public class OrderWebConfiguration {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return JSonMapper.objectMapper;
  }

  @Bean
  public Sampler defaultSampler() {
    return Sampler.ALWAYS_SAMPLE;
  }

}
