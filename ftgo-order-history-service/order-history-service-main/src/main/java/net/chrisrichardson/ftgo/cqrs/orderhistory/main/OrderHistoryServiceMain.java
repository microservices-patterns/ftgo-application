package net.chrisrichardson.ftgo.cqrs.orderhistory.main;

import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.OrderHistoryDynamoDBConfiguration;
import net.chrisrichardson.ftgo.cqrs.orderhistory.messaging.OrderHistoryServiceMessagingConfiguration;
import net.chrisrichardson.ftgo.cqrs.orderhistory.web.OrderHistoryWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({OrderHistoryWebConfiguration.class,
        OrderHistoryServiceMessagingConfiguration.class,
        OrderHistoryDynamoDBConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class,
        TramNoopDuplicateMessageDetectorConfiguration.class})
public class OrderHistoryServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(OrderHistoryServiceMain.class, args);
  }
}
