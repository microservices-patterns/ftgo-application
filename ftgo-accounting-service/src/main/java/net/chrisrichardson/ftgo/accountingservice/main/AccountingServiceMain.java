package net.chrisrichardson.ftgo.accountingservice.main;

import io.eventuate.javaclient.driver.EventuateDriverConfiguration;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import net.chrisrichardson.ftgo.accountingservice.messaging.AccountingMessagingConfiguration;
import net.chrisrichardson.ftgo.accountingservice.web.AccountingWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({AccountingMessagingConfiguration.class, AccountingWebConfiguration.class,
        TramCommandProducerConfiguration.class,
        EventuateDriverConfiguration.class,
        TramJdbcKafkaConfiguration.class})
public class AccountingServiceMain {

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

  public static void main(String[] args) {
    SpringApplication.run(AccountingServiceMain.class, args);
  }
}
