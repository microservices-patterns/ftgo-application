package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TramCommandProducerConfiguration.class, TramEventsPublisherConfiguration.class,})
public class SagaParticipantStubConfiguration {

  @Bean
  public SagaParticipantStubManager sagaParticipantStubManager(MessagingStubConfiguration messagingStubConfiguration, ChannelMapping channelMapping, MessageConsumer messageConsumer, MessageProducer messageProducer) {
    return new SagaParticipantStubManager(messagingStubConfiguration, channelMapping, messageConsumer, messageProducer);
  }

  @Bean
  public MessageTracker messageTracker(MessageTrackerConfiguration configuration, MessageConsumer messageConsumer) {
    return new MessageTracker(configuration, messageConsumer) ;
  }

  @Bean
  public SagaCommandProducer sagaCommandProducer() {
    return new SagaCommandProducer();
  }

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }


}
