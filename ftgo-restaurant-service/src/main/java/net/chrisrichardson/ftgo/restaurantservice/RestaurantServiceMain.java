package net.chrisrichardson.ftgo.restaurantservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.javaclient.commonimpl.JSonMapper;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import net.chrisrichardson.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@EnableAutoConfiguration
@Import({TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class})
@ComponentScan
public class RestaurantServiceMain {

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

  @Bean
  @Primary // conflicts with _halObjectMapper
  public ObjectMapper objectMapper() {
    return JSonMapper.objectMapper;
  }

  public static void main(String[] args) {
    SpringApplication.run(RestaurantServiceMain.class, args);
  }

}
