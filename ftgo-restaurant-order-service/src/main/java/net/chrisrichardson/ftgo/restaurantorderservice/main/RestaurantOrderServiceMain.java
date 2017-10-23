package net.chrisrichardson.ftgo.restaurantorderservice.main;

import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import net.chrisrichardson.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import net.chrisrichardson.ftgo.restaurantorderservice.messagehandlers.RestaurantMessageHandlersConfiguration;
import net.chrisrichardson.ftgo.restaurantorderservice.web.RestaurantOrderWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RestaurantOrderWebConfiguration.class, RestaurantMessageHandlersConfiguration.class,
        TramJdbcKafkaConfiguration.class,
        CommonSwaggerConfiguration.class})
public class RestaurantOrderServiceMain {

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

  public static void main(String[] args) {
    SpringApplication.run(RestaurantOrderServiceMain.class, args);
  }
}
